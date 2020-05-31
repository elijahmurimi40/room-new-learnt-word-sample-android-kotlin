package com.fortie40.newword.bindingadapters

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("onLongClick")
fun onLongCLick(view: View, onLongClick: () -> Unit) {
    view.setOnLongClickListener {
        onLongClick.invoke()
        it.isHapticFeedbackEnabled = false
        true
    }
}