package com.steventran.securenote

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class Note(@PrimaryKey val uuid: UUID = UUID.randomUUID(),
                var title: String = "",
                var body: String = "",
                @ColumnInfo(name = "last_edited") var lastEdited: Date = Date()) {

}