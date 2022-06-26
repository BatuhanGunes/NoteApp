package com.task.noteapp.feature_note.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.task.noteapp.R
import com.task.noteapp.feature_note.presentation.util.clearDiskCache
import com.task.noteapp.feature_note.presentation.util.clearGlideMemory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_note)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearGlideMemory()
        clearDiskCache()
    }
}