package com.task.noteapp.feature_note.domain.use_case

import com.task.noteapp.feature_note.domain.model.Note
import com.task.noteapp.feature_note.domain.repository.NoteRepository

class UpdateNote(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.updateNote(note)
    }
}