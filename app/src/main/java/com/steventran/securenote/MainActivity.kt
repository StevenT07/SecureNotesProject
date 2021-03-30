package com.steventran.securenote

import android.app.KeyguardManager
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity(), NotesListFragment.Callback,
    androidx.biometric.BiometricManager.Authenticators {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder().apply {
            setTitle("Secure Notes Verification")
            setSubtitle("Please verify your identity to access this application. ")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            }
            else {
                setAllowedAuthenticators(BIOMETRIC_STRONG)

            }
            setNegativeButtonText("Quit")
        }.build()
        val biometricManager = androidx.biometric.BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
                    or androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {

                    }
        }
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
                object: androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(applicationContext, "Authentication Succeeded.", Toast.LENGTH_SHORT).show()
                        initializeNoteFragment()
                    }
                })
        biometricPrompt.authenticate(promptInfo)
        setContentView(R.layout.activity_main)



    }

    override fun openNote(noteId: UUID) {
        val fragment = NotesFragment.newInstance(noteId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun initializeNoteFragment() {
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = NotesListFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }


}