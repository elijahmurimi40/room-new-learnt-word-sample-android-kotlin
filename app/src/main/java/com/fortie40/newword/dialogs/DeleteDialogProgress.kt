package com.fortie40.newword.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.fortie40.newword.NUMBER_OF_ITEMS_TO_DELETE
import com.fortie40.newword.R
import kotlinx.android.synthetic.main.delete_dialog_progress.view.*

class DeleteDialogProgress: AppCompatDialogFragment() {
    private var args: Bundle? = null
    private var numberOfItemsToDelete: Int = 0

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            args = arguments
            numberOfItemsToDelete = args!!.getInt(NUMBER_OF_ITEMS_TO_DELETE)
            val builder = AlertDialog.Builder(activity!!)
            val inflater = it!!.layoutInflater
            val view = inflater.inflate(R.layout.delete_dialog_progress, null)

            view.percentage.text = getString(R.string._0, "0")
            view.items.text = getString(R.string._1_1, "0", numberOfItemsToDelete.toString())
            builder.setView(view)

            builder.create()
        }
    }
}