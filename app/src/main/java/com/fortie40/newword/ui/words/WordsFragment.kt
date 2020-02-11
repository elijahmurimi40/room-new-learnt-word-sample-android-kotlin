package com.fortie40.newword.ui.words

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.SearchView
import android.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fortie40.newword.R
import com.fortie40.newword.databinding.WordsFragmentBinding
import com.fortie40.newword.dialogs.DeleteDialog
import com.fortie40.newword.helperfunctions.HelperFunctions
import com.fortie40.newword.roomdatabase.WordModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.words_fragment.*
import timber.log.Timber

class WordsFragment : Fragment(), WordAdapter.WordItemClickListener {

    private lateinit var wordsFragmentBinding: WordsFragmentBinding
    private lateinit var root: View
    private lateinit var viewModel: WordsViewModel
    private lateinit var wordAdapter: WordAdapter
    private lateinit var handler: Handler
    private lateinit var r: Runnable

    private var isInitialized: Boolean = false
    private var actionMode: ActionMode? = null
    private var numberOfItems: Int = 0

    private val allWords: List<WordModel>? by lazy {
        viewModel.allProducts.value
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // change toolbar title
        activity!!.title = getString(R.string.words)
        // back button
        activity!!.toolbar.navigationIcon = null

        wordsFragmentBinding = WordsFragmentBinding.inflate(inflater)
        root = wordsFragmentBinding.root
        setHasOptionsMenu(true)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // swipe refresh layout
        swipe_to_refresh.setColorSchemeResources(
            R.color.colorAccent, R.color.colorPrimary,
            R.color.colorPrimaryDark
        )
        swipe_to_refresh.setOnRefreshListener {
            getWords()
        }

        // navigate to add edit word fragment
        addNewWord.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_wordsFragment_to_addEditWordFragment)
        )
        viewModel = ViewModelProviders.of(this).get(WordsViewModel::class.java)
        getWords()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = getString(R.string.search_anything)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Timber.i("Query Submitted")
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                wordAdapter.filter.filter(HelperFunctions.toLowerCase(p0))
                isInitialized = true
                handler = Handler()
                r = Runnable { word_items.scrollToPosition(0) }
                handler.postDelayed(r, 300)
                return false
            }

        })
    }

    override fun onPause() {
        if (isInitialized) {
            handler.removeCallbacks(r)
        }
        super.onPause()
    }

    private fun getWords() {
        swipe_to_refresh.isRefreshing = true
        viewModel.allProducts.observe(viewLifecycleOwner, Observer { words ->
            if (words.isEmpty()) {
                no_words.visibility = View.VISIBLE
                swipe_to_refresh.isRefreshing = false
            } else {
                no_words.visibility = View.GONE
                for (word in words) {
                    Timber.d("***************************************************************")
                    Timber.d("ID: ${word.wordId}")
                    Timber.d("Word: ${word.wordLearned}")
                    Timber.d("***************************************************************")
                }
                wordAdapter = WordAdapter(this, words)
                words.let { wordAdapter.submitList(it) }
                word_items.layoutManager = LinearLayoutManager(activity)
                word_items.adapter = wordAdapter
                swipe_to_refresh.isRefreshing = false
            }
        })
    }

    private fun toggleSelection(position: Int) {
        wordAdapter.toggleSelection(position)
        val count = wordAdapter.getSelectedItemCount()

        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
            numberOfItems = count
        }
    }

    private fun openDialog(itemNumber: Int) {
        val deleteDialog = DeleteDialog(itemNumber)
        deleteDialog.show(activity!!.supportFragmentManager, "Delete Dialog")
    }

    override fun onWordClicked(clickedItemIndex: Int) {
        if (actionMode != null) {
            toggleSelection(clickedItemIndex)
        } else {
            val wordAtPosition = allWords?.get(clickedItemIndex)
            Timber.d("${wordAtPosition?.wordLearned}")

            val action =
                WordsFragmentDirections.actionWordsFragmentToAddEditWordFragment()
            action.id = wordAtPosition!!.wordId.toString()
            activity?.findNavController(R.id.nav_host_fragment)?.navigate(action)
        }
    }

    override fun onWordLongClicked(clickedItemIndex: Int) {
        Timber.d("$clickedItemIndex")
        val wordAtPosition = allWords?.get(clickedItemIndex)
        Timber.d("${wordAtPosition?.wordLearned}")
        if (actionMode == null) {
            actionMode = activity!!.startActionMode(ActionModeCallback())
        }
        toggleSelection(clickedItemIndex)
    }

    inner class ActionModeCallback : ActionMode.Callback {
        override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
            return when(p1?.itemId) {
                R.id.action_delete -> {
                    Timber.d("Selected")
                    openDialog(numberOfItems)
                    true
                }
                else -> false
            }
        }

        override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            p0?.menuInflater?.inflate(R.menu.add_edit_menu, p1)
            return true
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(p0: ActionMode?) {
            wordAdapter.clearSelection()
            actionMode = null
        }

    }

}
