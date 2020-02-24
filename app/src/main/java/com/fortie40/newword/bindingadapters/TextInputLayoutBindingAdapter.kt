package com.fortie40.newword.bindingadapters

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorMsg")
fun setErrorMessage(textInputLayout: TextInputLayout, string: String?) {
    textInputLayout.error = string
}