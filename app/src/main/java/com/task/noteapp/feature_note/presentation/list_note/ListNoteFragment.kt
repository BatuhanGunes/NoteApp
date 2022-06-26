package com.task.noteapp.feature_note.presentation.list_note

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.task.noteapp.R
import com.task.noteapp.feature_note.domain.model.Note
import com.task.noteapp.feature_note.presentation.NoteActivityViewModel
import com.task.noteapp.feature_note.presentation.list_note.adapters.NoteRecyclerViewAdapter
import com.task.noteapp.feature_note.presentation.util.SwipeToDeleteCallback
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ListNoteFragment : Fragment(R.layout.fragment_list_note) {

    private val viewModel by viewModels<NoteActivityViewModel>()
    private lateinit var rvAdapter: NoteRecyclerViewAdapter
    private lateinit var rvNote: RecyclerView
    private lateinit var emptyListTextView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view)
        val addNoteFab = view.findViewById<FloatingActionButton>(R.id.addNoteFab)
        emptyListTextView = view.findViewById(R.id.textViewEmptyList)
        rvNote = view.findViewById(R.id.rvNote)

        addNoteFab.setOnClickListener {
            val action = ListNoteFragmentDirections.actionListNoteFragmentToAddEditNoteFragment()
            navController.navigate(action)
        }

        recyclerViewDisplay()
        swipeToDelete(rvNote)
    }

    private fun recyclerViewDisplay() {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> setUpRecyclerView(2)
            else -> setUpRecyclerView(1)
        }
    }

    private fun setUpRecyclerView(spanCount: Int) {
        rvNote.apply {
            layoutManager =
                StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            rvAdapter = NoteRecyclerViewAdapter()
            adapter = rvAdapter
            postponeEnterTransition(300L, TimeUnit.MILLISECONDS)
            viewTreeObserver.addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
        }
        observerDataChanges()
    }

    private fun observerDataChanges() {
        viewModel.getAllNotes().observe(viewLifecycleOwner) { list ->
            emptyListTextView.isVisible = list.isEmpty()
            rvAdapter.submitList(list)
        }
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                showConfirmDeleteDialog(viewHolder)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showConfirmDeleteDialog(viewHolder: RecyclerView.ViewHolder) {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.delete_note_dialog_title)
            builder.setMessage(R.string.delete_note_dialog_message)
            builder.apply {
                setPositiveButton(R.string.delete_note_dialog_confirm) { _, _ ->
                    val position = viewHolder.adapterPosition
                    val note = rvAdapter.currentList[position]
                    rvAdapter.notifyItemRemoved(position)
                    viewModel.deleteNote(note)
                    showUndoDeleteSnackBar(note)
                }
                setNegativeButton(R.string.delete_note_dialog_cancel) { _, _ ->
                    rvAdapter.notifyItemChanged(viewHolder.adapterPosition)
                }
            }
            builder.create()
        }
        alertDialog?.setOnCancelListener {
            rvAdapter.notifyItemChanged(viewHolder.adapterPosition)
        }
        alertDialog?.show()
    }

    private fun showUndoDeleteSnackBar(note: Note) {
        val snackBar = Snackbar.make(
            requireView(), getString(R.string.delete_note_success), Snackbar.LENGTH_LONG
        ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onShown(transientBottomBar: Snackbar?) {
                transientBottomBar?.setAction(getString(R.string.delete_note_undo)) {
                    viewModel.insertNote(note)
                }
                super.onShown(transientBottomBar)
            }
        }).apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            setAnchorView(R.id.addNoteFab)
        }
        snackBar.setActionTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.yellow
            )
        )
        snackBar.show()
    }
}