package com.task.noteapp.feature_note.data.data_source

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.task.noteapp.test_utils.getOrAwaitValue
import com.task.noteapp.feature_note.domain.model.Note
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class NoteDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: NoteDatabase
    private lateinit var dao: NoteDao

    @Before
    fun setup() {
        hiltRule.inject()
        dao = database.noteDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun getNotes() = runBlockingTest {
        val noteItem1 = Note(
            id = 1,
            title = "insertNoteItem1",
            description = "noteItemDesc1",
            timestamp = "12345",
            imageUrl = "url",
            isEdited = false
        )

        val noteItem2 = Note(
            id = 2,
            title = "insertNoteItem2",
            description = "noteItemDesc2",
            timestamp = "12345",
            imageUrl = "url",
            isEdited = false
        )

        val noteItem3 = Note(
            id = 3,
            title = "insertNoteItem3",
            description = "noteItemDesc3",
            timestamp = "12345",
            imageUrl = "url",
            isEdited = false
        )

        dao.insertNote(noteItem1)
        dao.insertNote(noteItem2)
        dao.insertNote(noteItem3)

        val allNoteItems = dao.getNotes().getOrAwaitValue()
        assertThat(allNoteItems).contains(noteItem1)
        assertThat(allNoteItems).contains(noteItem2)
        assertThat(allNoteItems).contains(noteItem3)
        assertThat(allNoteItems).hasSize(3)
    }

    @Test
    fun upgradeNotes() = runBlockingTest {
        val noteItem = Note(
            id = 1,
            title = "title",
            description = "desc",
            timestamp = "12345",
            imageUrl = "url",
            isEdited = false
        )

        val noteItemUpgraded = Note(
            id = 1,
            title = "noteItemUpgraded",
            description = "noteItemUpgraded",
            timestamp = "54321",
            imageUrl = "lru",
            isEdited = true
        )

        dao.insertNote(noteItem)
        dao.updateNote(noteItemUpgraded)

        val allNoteItems = dao.getNotes().getOrAwaitValue()
        assertThat(allNoteItems).contains(noteItemUpgraded)
        assertThat(allNoteItems).doesNotContain(noteItem)
    }

    @Test
    fun insertNoteItem() = runBlockingTest {
        val noteItem = Note(
            id = 1,
            title = "title",
            description = "desc",
            timestamp = "12345",
            imageUrl = "url",
            isEdited = false
        )

        dao.insertNote(noteItem)

        val allNoteItems = dao.getNotes().getOrAwaitValue()
        assertThat(allNoteItems).contains(noteItem)
    }


    @Test
    fun deleteNoteItem() = runBlockingTest {
        val noteItem = Note(
            id = 1,
            title = "title",
            description = "desc",
            timestamp = "12345",
            imageUrl = "url",
            isEdited = false
        )

        dao.insertNote(noteItem)
        dao.deleteNote(noteItem)

        val allNoteItems = dao.getNotes().getOrAwaitValue()
        assertThat(allNoteItems).doesNotContain(noteItem)
    }
}