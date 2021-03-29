package com.steventran.securenote.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.steventran.securenote.Note

@Database(entities = arrayOf(Note::class), version = 1)
@TypeConverters(NotesTypeConverter::class)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun notesDAO(): NotesDAO

}