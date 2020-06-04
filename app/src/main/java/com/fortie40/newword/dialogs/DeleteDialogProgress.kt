package com.fortie40.newword.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.fortie40.newword.*
import com.fortie40.newword.interfaces.IDeleteWords
import kotlinx.android.synthetic.main.delete_dialog_progress.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteDialogProgress : AppCompatDialogFragment() {
    companion object {
        var deleteWordsListener: IDeleteWords? = null
        lateinit var dView: View
    }

    private var args: Bundle? = null
    private var numberOfItemsToDelete: Int = 0

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            args = arguments
            numberOfItemsToDelete = args!!.getInt(NUMBER_OF_ITEMS_TO_DELETE)
            val builder = AlertDialog.Builder(requireActivity())
            val inflater = it!!.layoutInflater
            dView = inflater.inflate(R.layout.delete_dialog_progress, null)

            dView.percentage.text = getString(R.string._0, 0)
            dView.items.text = getString(R.string._1_1, 0, numberOfItemsToDelete)
            dView.progress_bar.progress = PROGRESS_MIN
            dView.progress_bar.max = PROGRESS_MAX
            builder.setView(dView)

            builder.create()
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(IO).launch {
            deleteWordsListener?.deleteWords(numberOfItemsToDelete)
            closeDialog()
        }
    }

    private suspend fun closeDialog() {
        withContext(Main) {
            dialog?.dismiss()
            dialog?.cancel()
        }
    }
}