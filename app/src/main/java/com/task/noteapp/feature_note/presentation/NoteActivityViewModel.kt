package com.task.noteapp.feature_note.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.noteapp.feature_note.domain.model.Note
import com.task.noteapp.feature_note.domain.use_case.NoteUseCases
import com.task.noteapp.feature_note.presentation.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteActivityViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _insertNoteItemStatus = MutableLiveData<List<Resource<Note>>>()
    val insertNoteItemStatus: LiveData<List<Resource<Note>>> = _insertNoteItemStatus

    fun getAllNotes(): LiveData<List<Note>> = noteUseCases.getAllNotes()

    fun insertNote(note: Note) = viewModelScope.launch {
        if (note.title.isEmpty() || note.description.isEmpty() || note.timestamp.isEmpty()) {
            _insertNoteItemStatus.postValue(
                listOf(
                    Resource.error(
                        "The fields must not be empty",
                        null
                    )
                )
            )
            return@launch
        } else if ((note.imageUrl?.isNotEmpty() == true) && !note.imageUrl.startsWith("https://")) {
            _insertNoteItemStatus.postValue(
                listOf(
                    Resource.error(
                        "URL must start with https://",
                        null
                    )
                )
            )
            return@launch
        }

        noteUseCases.insertNote(note)
        _insertNoteItemStatus.postValue(listOf(Resource.success(note)))
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteUseCases.updateNote(note)
    }

    fun deleteNote(existingNote: Note) = viewModelScope.launch {
        noteUseCases.deleteNote(existingNote)
    }
}