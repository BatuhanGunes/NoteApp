package com.task.noteapp.feature_note.presentation.add_edit_note

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.task.noteapp.R
import com.task.noteapp.feature_note.domain.model.Note
import com.task.noteapp.feature_note.presentation.NoteActivityViewModel
import com.task.noteapp.feature_note.presentation.util.clearImageWithGlide
import com.task.noteapp.feature_note.presentation.util.hideKeyboard
import com.task.noteapp.feature_note.presentation.util.loadImageFromURL
import com.task.noteapp.feature_note.presentation.util.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditNoteFragment : Fragment(R.layout.fragment_add_edit_note) {

    private val viewModel by viewModels<NoteActivityViewModel>()
    private val currentDate = SimpleDateFormat.getDateInstance().format(Date())
    private val args: AddEditNoteFragmentArgs by navArgs()
    private var imagePath: String? = null
    private var isEditable: Boolean = true
    private lateinit var navController: NavController
    private lateinit var lastEdited: TextView
    private lateinit var noteImage: ImageView
    private lateinit var etNoteDesc: EditText
    private lateinit var etTitle: EditText
    private lateinit var editBtn: ImageView
    private lateinit var deleteBtn: ImageView
    private lateinit var noteEditAppBar: BottomAppBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Sets the unique transition name for the layout that is
         being inflated using SharedElementEnterTransition class */
        ViewCompat.setTransitionName(
            view.findViewById(R.id.addEditNoteFragmentParent),
            "recyclerView_${args.note?.id}"
        )

        val backBtn = view.findViewById<ImageView>(R.id.backBtn)
        val saveNoteFab = view.findViewById<FloatingActionButton>(R.id.saveNoteFab)

        navController = Navigation.findNavController(view)
        noteImage = view.findViewById(R.id.noteImage)
        lastEdited = view.findViewById(R.id.lastEdited)
        etNoteDesc = view.findViewById(R.id.etNoteDesc)
        etTitle = view.findViewById(R.id.etTitle)
        editBtn = view.findViewById(R.id.editBtn)
        deleteBtn = view.findViewById(R.id.deleteBtn)
        noteEditAppBar = view.findViewById(R.id.noteEditAppBar)

        backBtn.setOnClickListener { popBackStack() }
        saveNoteFab.setOnClickListener { saveNoteAndGoBack() }
        editBtn.setOnClickListener { setEditable(!isEditable) }
        deleteBtn.setOnClickListener { showConfirmDeleteDialog() }
        noteEditAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.clearPicture -> clearPicture()
                R.id.searchPicture -> showSearchPictureDialog()
                R.id.takePicture -> takePicture()
                R.id.selectPicture -> selectPicture()
            }
            true
        }

        etNoteDesc.setOnFocusChangeListener { _, _ -> requireView().hideKeyboard() }
        etTitle.setOnFocusChangeListener { _, _ -> requireView().hideKeyboard() }

        lastEdited.text = currentDate
        setUpNote()
    }

    private fun setUpNote() {
        args.note?.let { note ->
            deleteBtn.isVisible = true
            etTitle.setText(note.title)
            etNoteDesc.setText(note.description)
            if (note.isEdited) {
                lastEdited.text = getString(R.string.edited_on, note.timestamp)
            } else {
                lastEdited.text = getString(R.string.created_on, note.timestamp)
            }

            note.imageUrl?.let {
                imagePath = it
                noteImage.isVisible = true
                requireContext().loadImageFromURL(noteImage, it)
            }

            val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            etTitle.startAnimation(anim)
            etNoteDesc.startAnimation(anim)
            setEditable(false)
        }
    }

    private fun showConfirmDeleteDialog() {
        val alertDialog: AlertDialog? = activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.delete_note_dialog_title)
            builder.setMessage(R.string.delete_note_dialog_message)
            builder.apply {
                setPositiveButton(R.string.delete_note_dialog_confirm) { _, _ -> deleteNote() }
                setNegativeButton(R.string.delete_note_dialog_cancel) { _, _ -> }
            }
            builder.create()
        }
        alertDialog?.setCanceledOnTouchOutside(true)
        alertDialog?.show()
    }

    private fun deleteNote() {
        args.note?.let {
            viewModel.deleteNote(it)
            popBackStack()
        }
    }

    private fun setEditable(isEnabled: Boolean) {
        this.isEditable = isEnabled
        etTitle.isEnabled = isEnabled
        etNoteDesc.isEnabled = isEnabled
        noteEditAppBar.menu.setGroupVisible(0, isEnabled)

        if (isEnabled) editBtn.setImageResource(R.drawable.ic_baseline_lock_open_24)
        else editBtn.setImageResource(R.drawable.ic_baseline_lock_24)
    }

    /**
     * This Method handles the save or update operation.
     */
    private fun saveNoteAndGoBack() {
        when {
            etTitle.text.toString().isEmpty() -> showSnackBar(R.string.empty_error_title)
            etNoteDesc.text.toString().isEmpty() -> showSnackBar(R.string.empty_error_desc)
            else -> {
                when (val note = args.note) {
                    null -> {
                        viewModel.insertNote(
                            Note(
                                id = 0,
                                title = etTitle.text.toString(),
                                description = etNoteDesc.text.toString(),
                                timestamp = currentDate,
                                imageUrl = imagePath,
                                isEdited = false
                            )
                        )
                    }
                    else -> {
                        viewModel.updateNote(
                            Note(
                                id = note.id,
                                title = etTitle.text.toString(),
                                description = etNoteDesc.text.toString(),
                                timestamp = currentDate,
                                imageUrl = imagePath,
                                isEdited = true
                            )
                        )
                    }
                }
                popBackStack()
            }
        }
    }

    private fun clearPicture() {
        imagePath?.let {
            noteImage.isVisible = false
            showUndoClearPictureSnackBar(it)
            requireContext().clearImageWithGlide(noteImage)
        }
        imagePath = null
    }

    private fun showUndoClearPictureSnackBar(path: String) {
        val snackBar = Snackbar.make(
            requireView(), getString(R.string.clear_photo_success), Snackbar.LENGTH_LONG
        ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
            override fun onShown(transientBottomBar: Snackbar?) {
                transientBottomBar?.setAction(getString(R.string.clear_photo_undo)) {
                    imagePath = path
                    noteImage.isVisible = true
                    requireContext().loadImageFromURL(noteImage, path)
                }
                super.onShown(transientBottomBar)
            }
        }).apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            setAnchorView(R.id.saveNoteFab)
        }
        snackBar.setActionTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.yellow
            )
        )
        snackBar.show()
    }

    private fun showSearchPictureDialog() {
        val input = EditText(context)
        input.hint = getString(R.string.search_photo_dialog_hint)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val alertDialog: AlertDialog? = activity?.let { it ->
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.search_photo)
            builder.setView(input)
            builder.apply {
                setPositiveButton(R.string.search_photo_dialog_confirm) { _, _ ->
                    if (input.text.isNotEmpty() || input.text.isNotBlank()) {
                        if (input.text.startsWith("https://")) {
                            noteImage.isVisible = true
                            imagePath = input.text.toString()
                            imagePath?.let { path ->
                                requireContext().loadImageFromURL(noteImage, path)
                            }
                        } else{
                            showSnackBar(R.string.wrong_url_error)
                        }
                    } else {
                        showSnackBar(R.string.empty_error_url)
                    }
                }
                setNegativeButton(R.string.search_photo_dialog_cancel) { _, _ -> }
            }
            builder.create()
        }
        alertDialog?.setView(input, 50, 10, 50, 10)
        alertDialog?.show()
    }

    private fun takePicture() {
        // TODO: open the phone's camera and have the user take a picture
        showSnackBar(R.string.operation_not_supported)
    }

    private fun selectPicture() {
        // TODO: open the phone's gallery and have the user select a picture
        showSnackBar(R.string.operation_not_supported)
    }

    private fun popBackStack() {
        requireView().hideKeyboard()
        Navigation.findNavController(requireView()).popBackStack()
    }

    private fun showSnackBar(messageId: Int) {
        requireView().showSnackBar(R.id.saveNoteFab, getString(messageId))
    }
}