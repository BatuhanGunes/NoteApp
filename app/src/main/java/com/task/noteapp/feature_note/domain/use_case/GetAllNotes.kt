package com.task.noteapp.feature_note.domain.use_case

import androidx.lifecycle.LiveData
import com.task.noteapp.feature_note.domain.model.Note
import com.task.noteapp.feature_note.domain.repository.NoteRepository

class GetAllNotes(
    private val repository: NoteRepository
) {

    operator fun invoke(): LiveData<List<Note>> {
        return repository.getNotes()
    }
}