package com.avi.smartdailyexpensetracker.data.database.converters

import androidx.room.TypeConverter
import com.avi.smartdailyexpensetracker.data.entity.SyncStatus

class SyncStatusConverters {
    
    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String {
        return status.name
    }
    
    @TypeConverter
    fun toSyncStatus(status: String): SyncStatus {
        return try {
            SyncStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            SyncStatus.PENDING
        }
    }
}
