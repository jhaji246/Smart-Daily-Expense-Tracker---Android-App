package com.avi.smartdailyexpensetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDateTime

@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["syncStatus"]),
        Index(value = ["lastModified"]),
        Index(value = ["date"]),
        Index(value = ["category"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String,
    val amount: Double,
    val category: String,
    val notes: String? = null,
    val receiptImagePath: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val date: String = timestamp.toLocalDate().toString(),
    
    // Sync metadata
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val serverId: String? = null,
    val lastModified: LocalDateTime = LocalDateTime.now(),
    val isDeleted: Boolean = false,
    val version: Int = 1,
    
    // Offline metadata
    val offlineId: String = generateOfflineId(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        private fun generateOfflineId(): String {
            return "offline_${System.currentTimeMillis()}_${(0..9999).random()}"
        }
    }
}

enum class SyncStatus {
    PENDING,        // Needs to be synced
    SYNCED,         // Successfully synced
    FAILED,         // Sync failed, needs retry
    CONFLICT        // Has conflicts with server data
}

@Entity(
    tableName = "sync_logs",
    indices = [Index(value = ["timestamp"])]
)
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val operation: String,
    val entityType: String,
    val entityId: String,
    val status: SyncStatus,
    val errorMessage: String? = null,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val retryCount: Int = 0
)

@Entity(
    tableName = "offline_operations",
    indices = [Index(value = ["timestamp"])]
)
data class OfflineOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val operation: String,
    val entityType: String,
    val entityData: String, // JSON serialized data
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val priority: Int = 0, // Higher number = higher priority
    val isProcessed: Boolean = false
)
