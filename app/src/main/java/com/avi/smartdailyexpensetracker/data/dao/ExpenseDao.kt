package com.avi.smartdailyexpensetracker.data.dao

import androidx.room.*
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import com.avi.smartdailyexpensetracker.data.entity.SyncLogEntity
import com.avi.smartdailyexpensetracker.data.entity.OfflineOperationEntity
import com.avi.smartdailyexpensetracker.data.entity.SyncStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ExpenseDao {
    
    // Basic CRUD operations
    @Query("SELECT * FROM expenses WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE id = :id AND isDeleted = 0")
    suspend fun getExpenseById(id: Long): ExpenseEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long
    
    @Update
    suspend fun updateExpense(expense: ExpenseEntity)
    
    @Query("UPDATE expenses SET isDeleted = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteExpense(id: Long, timestamp: LocalDateTime = LocalDateTime.now())
    
    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun hardDeleteExpense(id: Long)
    
    // Sync-related queries
    @Query("SELECT * FROM expenses WHERE syncStatus = :status ORDER BY lastModified ASC")
    suspend fun getExpensesBySyncStatus(status: SyncStatus): List<ExpenseEntity>
    
    @Query("SELECT * FROM expenses WHERE syncStatus = :status AND isDeleted = 0 ORDER BY lastModified ASC LIMIT :limit")
    suspend fun getExpensesForSync(status: SyncStatus, limit: Int = 50): List<ExpenseEntity>
    
    @Query("SELECT COUNT(*) FROM expenses WHERE syncStatus = :status")
    suspend fun getCountBySyncStatus(status: SyncStatus): Int
    
    @Query("SELECT COUNT(*) FROM expenses WHERE syncStatus = :status AND isDeleted = 0")
    suspend fun getPendingSyncCount(status: SyncStatus): Int
    
    // Date-based queries
    @Query("SELECT * FROM expenses WHERE date = :date AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getExpensesByDate(date: String): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate AND isDeleted = 0 ORDER BY date DESC, timestamp DESC")
    fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<ExpenseEntity>>
    
    // Category-based queries
    @Query("SELECT * FROM expenses WHERE category = :category AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>
    
    @Query("SELECT category, COUNT(*) as count, SUM(amount) as totalAmount FROM expenses WHERE isDeleted = 0 GROUP BY category")
    fun getCategorySummary(): Flow<List<CategorySummary>>
    
    // Search queries
    @Query("SELECT * FROM expenses WHERE (title LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%') AND isDeleted = 0 ORDER BY timestamp DESC")
    fun searchExpenses(query: String): Flow<List<ExpenseEntity>>
    
    // Sync operations
    @Query("UPDATE expenses SET syncStatus = :status, serverId = :serverId, lastModified = :timestamp WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: SyncStatus, serverId: String? = null, timestamp: LocalDateTime = LocalDateTime.now())
    
    @Query("UPDATE expenses SET syncStatus = :status, lastModified = :timestamp WHERE serverId = :serverId")
    suspend fun updateSyncStatusByServerId(serverId: String, status: SyncStatus, timestamp: LocalDateTime = LocalDateTime.now())
    
    @Query("UPDATE expenses SET version = version + 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun incrementVersion(id: Long, timestamp: LocalDateTime = LocalDateTime.now())
    
    // Conflict resolution
    @Query("SELECT * FROM expenses WHERE serverId = :serverId AND version != :serverVersion AND isDeleted = 0")
    suspend fun getConflictingExpense(serverId: String, serverVersion: Int): ExpenseEntity?
    
    // Offline operations
    @Query("SELECT * FROM offline_operations WHERE isProcessed = 0 ORDER BY priority DESC, timestamp ASC")
    suspend fun getPendingOfflineOperations(): List<OfflineOperationEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfflineOperation(operation: OfflineOperationEntity): Long
    
    @Query("UPDATE offline_operations SET isProcessed = 1 WHERE id = :id")
    suspend fun markOfflineOperationProcessed(id: Long)
    
    // Sync logs
    @Insert
    suspend fun insertSyncLog(log: SyncLogEntity)
    
    @Query("SELECT * FROM sync_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSyncLogs(limit: Int = 100): List<SyncLogEntity>
    
    @Query("SELECT * FROM sync_logs WHERE status = :status ORDER BY timestamp DESC")
    suspend fun getSyncLogsByStatus(status: SyncStatus): List<SyncLogEntity>
    
    // Cleanup operations
    @Query("DELETE FROM sync_logs WHERE timestamp < :timestamp")
    suspend fun cleanupOldSyncLogs(timestamp: LocalDateTime)
    
    @Query("DELETE FROM offline_operations WHERE isProcessed = 1 AND timestamp < :timestamp")
    suspend fun cleanupProcessedOfflineOperations(timestamp: LocalDateTime)
    
    // Statistics
    @Query("SELECT COUNT(*) FROM expenses WHERE isDeleted = 0")
    suspend fun getTotalExpenseCount(): Int
    
    @Query("SELECT SUM(amount) FROM expenses WHERE isDeleted = 0")
    suspend fun getTotalExpenseAmount(): Double?
    
    @Query("SELECT COUNT(*) FROM expenses WHERE date = :date AND isDeleted = 0")
    suspend fun getExpenseCountByDate(date: String): Int
    
    @Query("SELECT SUM(amount) FROM expenses WHERE date = :date AND isDeleted = 0")
    suspend fun getExpenseAmountByDate(date: String): Double?
}

data class CategorySummary(
    val category: String,
    val count: Int,
    val totalAmount: Double
)
