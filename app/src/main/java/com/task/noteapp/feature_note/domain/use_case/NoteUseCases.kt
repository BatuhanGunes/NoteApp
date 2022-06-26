package com.task.noteapp.feature_note.domain.use_case

class NoteUseCases(
    val getAllNotes: GetAllNotes,
    val deleteNote: DeleteNote,
    val insertNote: InsertNote,
    val updateNote: UpdateNote
)