package com.steventran.securenote

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.steventran.securenote.database.NotesDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

class NotesRepository private constructor(context: Context){

    private val database: NotesDatabase = Room.databaseBuilder(
        context.applicationContext,
        NotesDatabase::class.java,
        "notes-database"
    ).build()

    private val notesDao = database.notesDAO()
    private val executor =Executors.newSingleThreadExecutor()

    fun getNote(uuid: UUID): LiveData<Note?> = notesDao.getNote(uuid)

    fun getAllNotes(): LiveData<List<Note?>> = notesDao.getAllNotes()

    fun addNote(note: Note) {
        executor.execute {
            notesDao.addNote(note  )
        }
    }
    fun updateNote(note: Note){
        executor.execute {
            notesDao.updateNote(note)
        }
    }

    fun deleteNotes(note: List<Note>) {
        executor.execute {
            notesDao.deleteNotes(note)
        }
    }

    companion object {
        private var INSTANCE: NotesRepository? = null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = NotesRepository(context)
            }

        }

        fun get(): NotesRepository {
            return INSTANCE ?:
                    throw IllegalStateException("Notes Repository needs to be initialized")
        }
    }


}