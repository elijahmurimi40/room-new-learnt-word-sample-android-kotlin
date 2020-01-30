package com.fortie40.newword.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.fortie40.newword.R
import timber.log.Timber

class DeleteDialog: AppCompatDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(ContextThemeWrapper(activity, R.style.Dialog))
            val inflater = it!!.layoutInflater
            val view = inflater.inflate(R.layout.dialog_layout, null)

            builder.setView(view)
                .setNegativeButton(getString(R.string.cancel_dialog)) { _, _ ->
                    dialog!!.cancel()
                }
                .setPositiveButton(getString(R.string.delete_dialog_)) { _, _ ->
                    Timber.d("Deleting")
                }

            builder.create()
        }
    }
}
