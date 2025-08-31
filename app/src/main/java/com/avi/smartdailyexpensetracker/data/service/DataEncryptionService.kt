package com.avi.smartdailyexpensetracker.data.service

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class DataEncryptionService(private val context: Context) {
    
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val MASTER_KEY_ALIAS = "expense_tracker_master_key"
        private const val ENCRYPTED_FILES_DIR = "encrypted_expenses"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 16
    }
    
    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .setUserAuthenticationRequired(false)
            .build()
    }
    
    // Encrypt expense data
    fun encryptExpenseData(data: String, expenseId: Long): String {
        val (encryptedFile, filePath) = createEncryptedFile(expenseId)
        
        return try {
            val outputStream = encryptedFile.openFileOutput()
            outputStream.write(data.toByteArray())
            outputStream.close()
            
            // Return the encrypted file path
            filePath
            
        } catch (e: Exception) {
            throw SecurityException("Failed to encrypt expense data", e)
        }
    }
    
    // Decrypt expense data
    fun decryptExpenseData(encryptedFilePath: String): String {
        val encryptedFile = EncryptedFile.Builder(
            context,
            File(encryptedFilePath),
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        
        return try {
            val inputStream = encryptedFile.openFileInput()
            val bytes = inputStream.readBytes()
            inputStream.close()
            
            String(bytes)
            
        } catch (e: Exception) {
            throw SecurityException("Failed to decrypt expense data", e)
        }
    }
    
    // Encrypt sensitive fields
    fun encryptSensitiveField(value: String): String {
        if (value.isBlank()) return value
        
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = generateSecretKey()
        
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(value.toByteArray())
        
        val iv = cipher.iv
        val combined = iv + encryptedBytes
        
        return android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT)
    }
    
    // Decrypt sensitive fields
    fun decryptSensitiveField(encryptedValue: String): String {
        if (encryptedValue.isBlank()) return encryptedValue
        
        try {
            val combined = android.util.Base64.decode(encryptedValue, android.util.Base64.DEFAULT)
            
            if (combined.size < GCM_IV_LENGTH + GCM_TAG_LENGTH) {
                return encryptedValue // Return original if decryption fails
            }
            
            val iv = combined.copyOfRange(0, GCM_IV_LENGTH)
            val encryptedBytes = combined.copyOfRange(GCM_IV_LENGTH, combined.size)
            
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val secretKey = generateSecretKey()
            
            val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes)
            
        } catch (e: Exception) {
            // Return original value if decryption fails
            return encryptedValue
        }
    }
    
    // Encrypt receipt images
    fun encryptReceiptImage(imagePath: String): String {
        val originalFile = File(imagePath)
        if (!originalFile.exists()) return imagePath
        
        val (encryptedFile, filePath) = createEncryptedFile(System.currentTimeMillis())
        
        try {
            val inputStream = FileInputStream(originalFile)
            val outputStream = encryptedFile.openFileOutput()
            
            inputStream.copyTo(outputStream)
            
            inputStream.close()
            outputStream.close()
            
            // Delete original unencrypted file
            originalFile.delete()
            
            return filePath
            
        } catch (e: Exception) {
            throw SecurityException("Failed to encrypt receipt image", e)
        }
    }
    
    // Decrypt receipt images
    fun decryptReceiptImage(encryptedImagePath: String): File {
        val encryptedFile = EncryptedFile.Builder(
            context,
            File(encryptedImagePath),
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        
        val decryptedFile = File(context.cacheDir, "decrypted_${System.currentTimeMillis()}.jpg")
        
        try {
            val inputStream = encryptedFile.openFileInput()
            val outputStream = FileOutputStream(decryptedFile)
            
            inputStream.copyTo(outputStream)
            
            inputStream.close()
            outputStream.close()
            
            return decryptedFile
            
        } catch (e: Exception) {
            throw SecurityException("Failed to decrypt receipt image", e)
        }
    }
    
    // Create encrypted file for expense
    private fun createEncryptedFile(expenseId: Long): Pair<EncryptedFile, String> {
        val encryptedDir = File(context.filesDir, ENCRYPTED_FILES_DIR)
        if (!encryptedDir.exists()) {
            encryptedDir.mkdirs()
        }
        
        val encryptedFile = File(encryptedDir, "expense_${expenseId}_${System.currentTimeMillis()}.enc")
        
        val encryptedFileInstance = EncryptedFile.Builder(
            context,
            encryptedFile,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        
        return Pair(encryptedFileInstance, encryptedFile.absolutePath)
    }
    
    // Generate secret key for field encryption
    private fun generateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
        keyStore.load(null)
        
        val keyAlias = "expense_field_key_${System.currentTimeMillis()}"
        
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            
            val keyGenSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
            .setUserAuthenticationRequired(false)
            .build()
            
            keyGenerator.init(keyGenSpec)
            keyGenerator.generateKey()
        }
        
        return keyStore.getKey(keyAlias, null) as SecretKey
    }
    
    // Clean up encrypted files
    fun cleanupEncryptedFiles() {
        val encryptedDir = File(context.filesDir, ENCRYPTED_FILES_DIR)
        if (encryptedDir.exists()) {
            encryptedDir.listFiles()?.forEach { file ->
                if (file.lastModified() < System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)) {
                    file.delete()
                }
            }
        }
    }
    
    // Check if encryption is available
    fun isEncryptionAvailable(): Boolean {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            true
        } catch (e: Exception) {
            false
        }
    }
}
