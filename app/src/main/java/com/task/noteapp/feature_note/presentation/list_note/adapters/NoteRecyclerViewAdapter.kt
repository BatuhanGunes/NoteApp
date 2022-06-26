package com.task.noteapp.feature_note.presentation.list_note.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.task.noteapp.R
import com.task.noteapp.feature_note.domain.model.Note
import com.task.noteapp.feature_note.presentation.list_note.ListNoteFragmentDirections
import com.task.noteapp.feature_note.presentation.util.DiffUtilCallback
import com.task.noteapp.feature_note.presentation.util.clearImageWithGlide
import com.task.noteapp.feature_note.presentation.util.loadImageFromURL

class NoteRecyclerViewAdapter : androidx.recyclerview.widget.ListAdapter<
        Note,
        NoteRecyclerViewAdapter.NotesViewHolder>(
    DiffUtilCallback()
) {

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: MaterialTextView = itemView.findViewById(R.id.noteItemTitle)
        val desc: TextView = itemView.findViewById(R.id.noteDescItemTitle)
        val date: MaterialTextView = itemView.findViewById(R.id.lastEdited)
        val image: ImageView = itemView.findViewById(R.id.itemNoteImage)
        val parent: MaterialCardView = itemView.findViewById(R.id.cardViewNoteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view_note_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        getItem(position).let { note ->
            holder.apply {
                parent.transitionName = "recyclerView_${note.id}"

                title.text = note.title
                desc.text = note.description

                if (note.imageUrl != null) {
                    image.isVisible = true
                    itemView.context.loadImageFromURL(image, note.imageUrl)
                } else {
                    itemView.context.clearImageWithGlide(image)
                }

                if (note.isEdited) {
                    date.text =
                        holder.itemView.context.getString(R.string.edited_on, note.timestamp)
                    date.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_baseline_edit_24, 0, 0, 0
                    )
                } else {
                    date.text =
                        holder.itemView.context.getString(R.string.created_on, note.timestamp)
                }

                itemView.setOnClickListener {
                    val action =
                        ListNoteFragmentDirections.actionListNoteFragmentToAddEditNoteFragment()
                            .setNote(note)
                    val extras = FragmentNavigatorExtras(parent to "recyclerView_${note.id}")
                    Navigation.findNavController(it).navigate(action, extras)
                }
            }
        }
    }
}