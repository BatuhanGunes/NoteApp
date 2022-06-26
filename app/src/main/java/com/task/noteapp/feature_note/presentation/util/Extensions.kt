package com.task.noteapp.feature_note.presentation.util

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.task.noteapp.R

fun View.hideKeyboard() =
    (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, HIDE_NOT_ALWAYS)

fun Context.loadImageFromURL(image: ImageView, url: String) =
    Glide.with(this.applicationContext)
        .load(url)
        .error(R.drawable.ic_baseline_no_photography_24)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .dontAnimate()
        .into(image)

fun Context.clearImageWithGlide(image: ImageView) =
    Glide.with(this)
        .clear(image)

fun Context.clearGlideMemory() = Thread {
    Glide.get(this).clearDiskCache()
}.start()

fun Context.clearDiskCache() = Glide.get(this).clearMemory()

fun View.showSnackBar(anchorViewId: Int, message: String) =
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .apply {
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            setAnchorView(anchorViewId)
        }.show()
