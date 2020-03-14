package com.fortie40.newword.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.fortie40.newword.R

class DeleteDialogProgress: AppCompatDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(activity!!)
            val inflater = it!!.layoutInflater
            val view = inflater.inflate(R.layout.delete_dialog_progress, null)
            builder.setView(view)

            builder.create()
        }
    }
}