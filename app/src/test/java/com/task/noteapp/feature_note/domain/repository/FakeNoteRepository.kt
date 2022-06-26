package com.task.noteapp.feature_note.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.task.noteapp.feature_note.domain.model.Note

class FakeNoteRepository : NoteRepository {

    private val noteItems = mutableListOf<Note>()
    private val observableNoteItems = MutableLiveData<List<Note>>(noteItems)

    private fun refreshLiveData() {
        observableNoteItems.postValue(noteItems)
    }

    override fun getNotes(): LiveData<List<Note>> {
        return observableNoteItems
    }

    override suspend fun insertNote(note: Note) {
        noteItems.add(note)
        refreshLiveData()
    }

    override suspend fun updateNote(note: Note) {
        noteItems[note.id] = note
    }

    override suspend fun deleteNote(note: Note) {
        noteItems.remove(note)
        refreshLiveData()
    }
}