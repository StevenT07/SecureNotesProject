package com.steventran.securenote.database

import androidx.room.TypeConverter
import java.util.*

class NotesTypeConverter {

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(milisSinceEpoch: Long?): Date? {
        return milisSinceEpoch?.let { Date(it) }
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }
}