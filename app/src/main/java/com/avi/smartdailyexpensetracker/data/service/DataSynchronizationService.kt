package com.avi.smartdailyexpensetracker.data.service

import android.content.Context
import androidx.work.*
import com.avi.smartdailyexpensetracker.data.dao.ExpenseDao
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import com.avi.smartdailyexpensetracker.data.entity.SyncLogEntity
import com.avi.smartdailyexpensetracker.data.entity.OfflineOperationEntity
import com.avi.smartdailyexpensetracker.data.entity.SyncStatus
import com.avi.smartdailyexpensetracker.data.worker.ExpenseSyncWorker
import com.avi.smartdailyexpensetracker.data.worker.ExpenseCleanupWorker
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class DataSynchronizationService(
    private val context: Context,
    private val expenseDao: ExpenseDao
) {
    
    companion object {
        private const val SYNC_WORK_NAME = "expense_sync_work"
        private const val CLEANUP_WORK_NAME = "expense_cleanup_work"
        private const val SYNC_INTERVAL_HOURS = 6L
        private const val CLEANUP_INTERVAL_DAYS = 7L
    }
    
    // Start periodic sync
    fun startPeriodicSync() {
        val syncWorkRequest = PeriodicWorkRequestBuilder<ExpenseSyncWorker>(
            SYNC_INTERVAL_HOURS, TimeUnit.HOURS
        ).apply {
            setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
        }.build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
    
    // Start periodic cleanup
    fun startPeriodicCleanup() {
        val cleanupWorkRequest = PeriodicWorkRequestBuilder<ExpenseCleanupWorker>(
            CLEANUP_INTERVAL_DAYS, TimeUnit.DAYS
        ).apply {
            setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
        }.build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            CLEANUP_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupWorkRequest
        )
    }
    
    // Manual sync trigger
    fun triggerManualSync() {
        val manualSyncWork = OneTimeWorkRequestBuilder<ExpenseSyncWorker>().apply {
            setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
        }.build()
        
        WorkManager.getInstance(context).enqueue(manualSyncWork)
    }
    
    // Stop all sync operations
    fun stopSync() {
        WorkManager.getInstance(context).cancelAllWork()
    }
    
    // Get sync status
    suspend fun getSyncStatus(): SyncStatusSummary {
        val pendingCount = expenseDao.getPendingSyncCount(SyncStatus.PENDING)
        val failedCount = expenseDao.getPendingSyncCount(SyncStatus.FAILED)
        val conflictCount = expenseDao.getPendingSyncCount(SyncStatus.CONFLICT)
        val totalCount = expenseDao.getTotalExpenseCount()
        
        return SyncStatusSummary(
            pendingCount = pendingCount,
            failedCount = failedCount,
            conflictCount = conflictCount,
            totalCount = totalCount,
            lastSyncTime = getLastSyncTime()
        )
    }
    
    // Process offline operations
    suspend fun processOfflineOperations() {
        val pendingOperations = expenseDao.getPendingOfflineOperations()
        
        pendingOperations.forEach { operation ->
            try {
                when (operation.operation) {
                    "INSERT" -> processInsertOperation(operation)
                    "UPDATE" -> processUpdateOperation(operation)
                    "DELETE" -> processDeleteOperation(operation)
                }
                
                expenseDao.markOfflineOperationProcessed(operation.id)
                logSyncOperation(operation.operation, operation.entityType, operation.id.toString(), SyncStatus.SYNCED)
                
            } catch (e: Exception) {
                logSyncOperation(operation.operation, operation.entityType, operation.id.toString(), SyncStatus.FAILED, e.message)
            }
        }
    }
    
    // Sync pending expenses
    suspend fun syncPendingExpenses() {
        val pendingExpenses = expenseDao.getExpensesForSync(SyncStatus.PENDING, 50)
        
        pendingExpenses.forEach { expense ->
            try {
                // Simulate server sync (replace with actual API call)
                val serverId = simulateServerSync(expense)
                
                expenseDao.updateSyncStatus(
                    expense.id,
                    SyncStatus.SYNCED,
                    serverId,
                    LocalDateTime.now()
                )
                
                logSyncOperation("SYNC", "EXPENSE", expense.id.toString(), SyncStatus.SYNCED)
                
            } catch (e: Exception) {
                expenseDao.updateSyncStatus(
                    expense.id,
                    SyncStatus.FAILED,
                    null,
                    LocalDateTime.now()
                )
                
                logSyncOperation("SYNC", "EXPENSE", expense.id.toString(), SyncStatus.FAILED, e.message)
            }
        }
    }
    
    // Resolve conflicts
    suspend fun resolveConflicts(): List<ExpenseConflict> {
        val conflicts = mutableListOf<ExpenseConflict>()
        
        // Get all expenses with server IDs
        val syncedExpenses = expenseDao.getExpensesBySyncStatus(SyncStatus.SYNCED)
        
        syncedExpenses.forEach { expense ->
            expense.serverId?.let { serverId ->
                // Simulate conflict detection (replace with actual server check)
                val serverVersion = simulateServerVersionCheck(serverId)
                
                if (expense.version != serverVersion) {
                    val conflict = ExpenseConflict(
                        localExpense = expense,
                        serverId = serverId,
                        localVersion = expense.version,
                        serverVersion = serverVersion
                    )
                    conflicts.add(conflict)
                    
                    expenseDao.updateSyncStatus(
                        expense.id,
                        SyncStatus.CONFLICT,
                        serverId,
                        LocalDateTime.now()
                    )
                }
            }
        }
        
        return conflicts
    }
    
    // Cleanup old data
    suspend fun cleanupOldData() {
        val cutoffDate = LocalDateTime.now().minusDays(30)
        
        expenseDao.cleanupOldSyncLogs(cutoffDate)
        expenseDao.cleanupProcessedOfflineOperations(cutoffDate)
    }
    
    // Private helper methods
    private suspend fun processInsertOperation(operation: OfflineOperationEntity) {
        // Parse operation data and insert
        // This would typically involve JSON deserialization
    }
    
    private suspend fun processUpdateOperation(operation: OfflineOperationEntity) {
        // Parse operation data and update
        // This would typically involve JSON deserialization
    }
    
    private suspend fun processDeleteOperation(operation: OfflineOperationEntity) {
        // Parse operation data and delete
        // This would typically involve JSON deserialization
    }
    
    private suspend fun simulateServerSync(expense: ExpenseEntity): String {
        // Simulate network delay
        delay(100)
        // Return mock server ID
        return "server_${expense.id}_${System.currentTimeMillis()}"
    }
    
    private suspend fun simulateServerVersionCheck(serverId: String): Int {
        // Simulate network delay
        delay(50)
        // Return mock server version
        return (1..10).random()
    }
    
    private suspend fun getLastSyncTime(): LocalDateTime? {
        val recentLogs = expenseDao.getRecentSyncLogs(1)
        return recentLogs.firstOrNull()?.timestamp
    }
    
    private suspend fun logSyncOperation(
        operation: String,
        entityType: String,
        entityId: String,
        status: SyncStatus,
        errorMessage: String? = null
    ) {
        val log = SyncLogEntity(
            operation = operation,
            entityType = entityType,
            entityId = entityId,
            status = status,
            errorMessage = errorMessage
        )
        expenseDao.insertSyncLog(log)
    }
}

// Data classes for sync status
data class SyncStatusSummary(
    val pendingCount: Int,
    val failedCount: Int,
    val conflictCount: Int,
    val totalCount: Int,
    val lastSyncTime: LocalDateTime?
)

data class ExpenseConflict(
    val localExpense: ExpenseEntity,
    val serverId: String,
    val localVersion: Int,
    val serverVersion: Int
)
