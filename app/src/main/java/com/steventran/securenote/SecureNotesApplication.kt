package com.steventran.securenote

import android.app.Application

class SecureNotesApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        NotesRepository.initialize(this)
    }
}