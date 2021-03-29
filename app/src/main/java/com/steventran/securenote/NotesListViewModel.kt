package com.steventran.securenote

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class NotesListViewModel : ViewModel() {
    private val notesRepository: NotesRepository = NotesRepository.get()
    val notesLiveData: LiveData<List<Note?>> = notesRepository.getAllNotes()


    fun addNote(note: Note) {
        notesRepository.addNote(note)
    }

    fun deleteNotes(notes: List<Note>) {
        notesRepository.deleteNotes(notes)
    }
}