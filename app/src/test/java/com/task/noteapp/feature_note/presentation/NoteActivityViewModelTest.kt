package com.task.noteapp.feature_note.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.task.noteapp.feature_note.domain.model.Note
import com.task.noteapp.feature_note.domain.repository.FakeNoteRepository
import com.task.noteapp.feature_note.domain.use_case.*
import com.task.noteapp.feature_note.presentation.util.Status
import com.task.noteapp.test_utils.MainCoroutineRule
import com.task.noteapp.test_utils.getOrAwaitValueTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NoteActivityViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: NoteActivityViewModel

    @Before
    fun setup() {
        viewModel = NoteActivityViewModel(
            NoteUseCases(
                getAllNotes = GetAllNotes(FakeNoteRepository()),
                deleteNote = DeleteNote(FakeNoteRepository()),
                insertNote = InsertNote(FakeNoteRepository()),
                updateNote = UpdateNote(FakeNoteRepository())
            )
        )
    }

    @Test
    fun `insert note item with empty field, returns error`() {
        viewModel.insertNote(
            Note(1, "", "", "", "", false)
        )

        val value = viewModel.insertNoteItemStatus.getOrAwaitValueTest()
        assertThat(value[0].status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert note item with invalid url input, returns error`() {
        viewModel.insertNote(
            Note(
                id= 1,
                title = "title",
                description = "desc",
                timestamp = "12345",
                imageUrl = "url",
                isEdited = false
            )
        )

        val value = viewModel.insertNoteItemStatus.getOrAwaitValueTest()
        assertThat(value[0].status).isEqualTo(Status.ERROR)
    }

    @Test
    fun `insert note item with valid url input, returns success`() {
        viewModel.insertNote(
            Note(
                id = 1,
                title = "title",
                description = "desc",
                timestamp = "12345",
                imageUrl = "https://",
                isEdited = false
            )
        )

        val value = viewModel.insertNoteItemStatus.getOrAwaitValueTest()
        assertThat(value[0].status).isEqualTo(Status.SUCCESS)
    }
}