package com.fortie40.newword.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.fortie40.newword.R
import timber.log.Timber

class DeleteDialog(): AppCompatDialogFragment() {
    private var numberOfItems: Int = 1
    constructor(itemNumber: Int): this() {
        numberOfItems = itemNumber
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(activity!!)
            val inflater = it!!.layoutInflater
            val view = inflater.inflate(R.layout.dialog_layout, null)

            val dialogTitle = view.findViewById<TextView>(R.id.delete_dialog_title)
            val dialogContent = view.findViewById<TextView>(R.id.delete_dialog_content)
            if (numberOfItems == 1) {
                dialogTitle.text = getString(R.string.delete_word)
                dialogContent.text = getString(R.string._1_word_will_be_permanently_deleted)
            } else {
                dialogTitle.text = getString(R.string.delete_words)
                dialogContent.text = getString(R.string.words_will_be_permanently_deleted,
                    numberOfItems.toString())
            }

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
