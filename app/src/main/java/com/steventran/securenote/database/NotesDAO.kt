package com.steventran.securenote.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.steventran.securenote.Note
import java.util.*

@Dao
interface NotesDAO {

    @Insert
    fun addNote(note: Note)

    @Delete
    fun deleteNotes(note: List<Note>)



    @Update
    fun updateNote(note: Note)

    @Query("SELECT * from note WHERE uuid=(:uuid)")
    fun getNote(uuid: UUID): LiveData<Note?>


    @Query("SELECT * from note")
    fun getAllNotes(): LiveData<List<Note?>>


}