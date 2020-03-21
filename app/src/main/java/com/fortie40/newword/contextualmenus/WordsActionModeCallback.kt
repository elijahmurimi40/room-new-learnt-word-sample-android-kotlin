package com.fortie40.newword.contextualmenus

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.fortie40.newword.R
import com.fortie40.newword.interfaces.IWordsActionModeListener

class WordsActionModeCallback(listener: IWordsActionModeListener) :
    ActionMode.Callback {
    private val _listener = listener

    override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
        return when (p1?.itemId) {
            R.id.action_delete -> {
                _listener.openDeleteDialog()
                true
            }
            else -> false
        }
    }

    override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        _listener.onCreateActionMode()
        p0?.menuInflater?.inflate(R.menu.add_edit_menu, p1)
        return true
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        _listener.onDestroyActionMode()
    }
}