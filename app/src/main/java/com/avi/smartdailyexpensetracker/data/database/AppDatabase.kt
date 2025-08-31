package com.avi.smartdailyexpensetracker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.avi.smartdailyexpensetracker.data.dao.ExpenseDao
import com.avi.smartdailyexpensetracker.data.entity.ExpenseEntity
import com.avi.smartdailyexpensetracker.data.entity.SyncLogEntity
import com.avi.smartdailyexpensetracker.data.entity.OfflineOperationEntity
import com.avi.smartdailyexpensetracker.data.database.converters.DateTimeConverters
import com.avi.smartdailyexpensetracker.data.database.converters.SyncStatusConverters

@Database(
    entities = [
        ExpenseEntity::class,
        SyncLogEntity::class,
        OfflineOperationEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(
    DateTimeConverters::class,
    SyncStatusConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun expenseDao(): ExpenseDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_expense_tracker.db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Initialize database with default data if needed
                    }
                    
                    override fun onOpen(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onOpen(db)
                        // Enable foreign key constraints
                        db.execSQL("PRAGMA foreign_keys=ON")
                    }
                })
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Create new tables for sync support
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `sync_logs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `operation` TEXT NOT NULL,
                        `entityType` TEXT NOT NULL,
                        `entityId` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `errorMessage` TEXT,
                        `timestamp` TEXT NOT NULL,
                        `retryCount` INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `offline_operations` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `operation` TEXT NOT NULL,
                        `entityType` TEXT NOT NULL,
                        `entityData` TEXT NOT NULL,
                        `timestamp` TEXT NOT NULL,
                        `priority` INTEGER NOT NULL DEFAULT 0,
                        `isProcessed` INTEGER NOT NULL DEFAULT 0
                    )
                """)
                
                // Add new columns to expenses table
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `syncStatus` TEXT NOT NULL DEFAULT 'PENDING'")
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `serverId` TEXT")
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `lastModified` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `isDeleted` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `version` INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `offlineId` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `createdAt` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `updatedAt` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `expenses` ADD COLUMN `date` TEXT NOT NULL DEFAULT ''")
                
                // Create indices for better performance
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_syncStatus` ON `expenses` (`syncStatus`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_lastModified` ON `expenses` (`lastModified`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_date` ON `expenses` (`date`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_category` ON `expenses` (`category`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_sync_logs_timestamp` ON `sync_logs` (`timestamp`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_offline_operations_timestamp` ON `offline_operations` (`timestamp`)")
                
                // Update existing data
                database.execSQL("""
                    UPDATE expenses 
                    SET lastModified = timestamp, 
                        createdAt = timestamp, 
                        updatedAt = timestamp,
                        date = DATE(timestamp)
                    WHERE lastModified = ''
                """)
            }
        }
    }
}
