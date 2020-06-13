package com.fortie40.newword

import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fortie40.newword.databinding.ActivityMainBinding
import com.fortie40.newword.dialogs.DeleteDialog
import com.fortie40.newword.dialogs.DeleteDialogProgress
import com.fortie40.newword.ui.words.WordsFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var optionMenu: Menu
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Timber tree
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        optionMenu = menu
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        searchView = optionMenu.findItem(R.id.action_search).actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        searchView.maxWidth = Integer.MAX_VALUE
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onContextMenuClosed(menu: Menu) {
        super.onContextMenuClosed(menu)
        WordsFragment.newInstance().resetTrackerAndActionMode()
        Timber.d("Closed")
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchView.isIconified = true
        } else {
            super.onBackPressed()
        }
    }

    override fun onStop() {
        DeleteDialog.deleteDialogListener = null
        DeleteDialogProgress.deleteProgressDialog = null
        super.onStop()
    }
}
