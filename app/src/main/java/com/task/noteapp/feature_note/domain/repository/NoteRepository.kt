package com.task.noteapp.feature_note.domain.repository

import androidx.lifecycle.LiveData
import com.task.noteapp.feature_note.domain.model.Note

interface NoteRepository {

    fun getNotes(): LiveData<List<Note>>

    suspend fun insertNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)
}