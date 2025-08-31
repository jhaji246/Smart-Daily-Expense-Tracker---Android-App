package com.avi.smartdailyexpensetracker.data.worker

import android.content.Context
import androidx.work.*
import com.avi.smartdailyexpensetracker.data.database.AppDatabase
import com.avi.smartdailyexpensetracker.data.service.DataSynchronizationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ExpenseCleanupWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val syncService = DataSynchronizationService(applicationContext, database.expenseDao())
            
            // Cleanup old sync logs and offline operations
            syncService.cleanupOldData()
            
            // Log successful cleanup
            database.expenseDao().insertSyncLog(
                com.avi.smartdailyexpensetracker.data.entity.SyncLogEntity(
                    operation = "CLEANUP",
                    entityType = "SYSTEM",
                    entityId = "ALL",
                    status = com.avi.smartdailyexpensetracker.data.entity.SyncStatus.SYNCED,
                    timestamp = LocalDateTime.now()
                )
            )
            
            // Return success
            Result.success()
            
        } catch (e: Exception) {
            // Log error
            val database = AppDatabase.getDatabase(applicationContext)
            database.expenseDao().insertSyncLog(
                com.avi.smartdailyexpensetracker.data.entity.SyncLogEntity(
                    operation = "CLEANUP",
                    entityType = "SYSTEM",
                    entityId = "ALL",
                    status = com.avi.smartdailyexpensetracker.data.entity.SyncStatus.FAILED,
                    errorMessage = e.message,
                    timestamp = LocalDateTime.now()
                )
            )
            
            // Return failure for cleanup errors
            Result.failure()
        }
    }
    
    companion object {
        fun createWorkRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ExpenseCleanupWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .build()
        }
    }
}
