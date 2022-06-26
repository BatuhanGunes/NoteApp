package com.task.noteapp.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val description: String,
    val timestamp: String,
    val imageUrl: String?,
    val isEdited: Boolean
) : Serializable
