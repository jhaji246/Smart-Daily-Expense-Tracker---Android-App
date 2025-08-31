package com.avi.smartdailyexpensetracker.data.worker

import android.content.Context
import androidx.work.*
import com.avi.smartdailyexpensetracker.data.database.AppDatabase
import com.avi.smartdailyexpensetracker.data.service.DataSynchronizationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ExpenseSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val syncService = DataSynchronizationService(applicationContext, database.expenseDao())
            
            // Process offline operations first
            syncService.processOfflineOperations()
            
            // Sync pending expenses
            syncService.syncPendingExpenses()
            
            // Resolve any conflicts
            val conflicts = syncService.resolveConflicts()
            
            // Log successful sync
            database.expenseDao().insertSyncLog(
                com.avi.smartdailyexpensetracker.data.entity.SyncLogEntity(
                    operation = "PERIODIC_SYNC",
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
                    operation = "PERIODIC_SYNC",
                    entityType = "SYSTEM",
                    entityId = "ALL",
                    status = com.avi.smartdailyexpensetracker.data.entity.SyncStatus.FAILED,
                    errorMessage = e.message,
                    timestamp = LocalDateTime.now()
                )
            )
            
            // Return retry if it's a network issue, failure otherwise
            if (e is java.net.UnknownHostException || e is java.net.SocketTimeoutException) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
    
    companion object {
        fun createWorkRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ExpenseSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.HOURS)
                .build()
        }
    }
}
