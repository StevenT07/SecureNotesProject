package com.steventran.securenote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class NotesViewModel: ViewModel() {
        private val notesRepository = NotesRepository.get()
    private val noteIdLiveData = MutableLiveData<UUID>()

    var noteLiveData: LiveData<Note?> =
        Transformations.switchMap(noteIdLiveData) {noteId ->
            notesRepository.getNote(noteId)
        }

    fun loadNote(id: UUID) {
        noteIdLiveData.value = id
    }

    fun saveNote(note: Note) {
        notesRepository.updateNote(note)
    }

}