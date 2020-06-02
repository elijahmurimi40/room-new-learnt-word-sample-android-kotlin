package com.fortie40.newword.ui.words

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.fortie40.newword.*
import com.fortie40.newword.contextualmenus.WordsActionModeCallback
import com.fortie40.newword.databinding.WordsFragmentBinding
import com.fortie40.newword.dialogs.DeleteDialog
import com.fortie40.newword.dialogs.DeleteDialogProgress
import com.fortie40.newword.helperclasses.HelperFunctions
import com.fortie40.newword.interfaces.IClickListener
import com.fortie40.newword.interfaces.IDeleteDialogListener
import com.fortie40.newword.interfaces.IDeleteWords
import com.fortie40.newword.interfaces.IWordsActionModeListener
import com.fortie40.newword.roomdatabase.WordModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.delete_dialog_progress.view.*
import kotlinx.android.synthetic.main.words_fragment.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

class WordsFragment :
    Fragment(), IClickListener, IDeleteDialogListener, IWordsActionModeListener, IDeleteWords {

    private lateinit var wordsFragmentBinding: WordsFragmentBinding
    private lateinit var root: View
    private lateinit var wordAdapter: WordsAdapter
    private lateinit var handler: Handler
    private lateinit var recyclerViewScrollToPosition: Runnable
    private lateinit var searchView: SearchView

    private var isInitialized: Boolean = false
    private var actionMode: ActionMode? = null
    private var isInActionMode: Boolean = false
    private var tracker: SelectionTracker<Long>? = null
    private var _savedInstanceState: Bundle? = null

    private var typeOfDeletion: String = ""

    private val viewModel by viewModels<WordsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // change toolbar title
        requireActivity().title = getString(R.string.words)
        // back button
        requireActivity().toolbar.navigationIcon = null

        wordsFragmentBinding = WordsFragmentBinding.inflate(inflater)
        root = wordsFragmentBinding.root
        setHasOptionsMenu(true)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _savedInstanceState = savedInstanceState

        wordsFragmentBinding.apply {
            this.lifecycleOwner = viewLifecycleOwner
        }
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
        getWords()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        tracker?.onSaveInstanceState(outState)
        outState.putBoolean(IS_IN_ACTION_MODE, isInActionMode)
        super.onSaveInstanceState(outState)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = getString(R.string.search_anything)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchWord(p0)
                return false
            }

            override fun onQueryTextChange(p0: String): Boolean {
                searchWord(p0)
                return false
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_all_words -> openDeleteDialog(wordAdapter.itemCount)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        DeleteDialog.deleteDialogListener = this
        DeleteDialogProgress.deleteWordsListener = this
    }

    override fun onPause() {
        if (isInitialized) {
            handler.removeCallbacks(recyclerViewScrollToPosition)
        }
        super.onPause()
    }

    override fun onWordClick(wordModel: WordModel) {
        if (actionMode != null) {
            return
        }
        val action =
            WordsFragmentDirections.actionWordsFragmentToAddEditWordFragment()
        action.wordM = wordModel
        requireActivity().findNavController(R.id.nav_host_fragment).navigate(action)
    }

    override fun onWordLongClick() {
        Timber.i("I was Long Clicked")
    }

    override fun onDeletePressed() {
        if (actionMode != null) {
            openDeleteDialogProgress(tracker!!.selection.size(), DELETE_ICON_PRESSED)
        } else {
            openDeleteDialogProgress(wordAdapter.itemCount, DELETE_ALL_WORDS)
        }
    }

    override fun openDeleteDialog() {
        openDeleteDialog(tracker!!.selection.size())
    }

    override fun onCreateActionMode() {
        isInActionMode = true
    }

    override fun onDestroyActionMode() {
        resetTrackerAndActionMode()
    }

    override suspend fun deleteWords(view: View, n: Int) {
        delay(200)
        val wordIdList = getWordIdList()
        for (i in 1..n) {
            val progress = ((i.toFloat() / n) * 100).toInt()
            val wordId = wordIdList[i - 1]
            viewModel.deleteWordById(wordId)
            withContext(Main) {
                view.percentage.text = getString(R.string._0, progress)
                view.items.text = getString(R.string._1_1, i, n)
                view.progress_bar.progress = progress

                resetTrackerAndActionMode()
            }
            delay(500)
        }
        delay(300)
    }

    private fun getWordIdList(): List<Int> {
        val wm = viewModel.allWords.value!!
        var wordId: Int
        val list = ArrayList<Int>()
        if (typeOfDeletion == DELETE_ICON_PRESSED) {
            tracker!!.selection.map {
                wordId = wm[it.toInt()].wordId!!
                list.add(wordId)
            }
        } else {
            wm.map {
                wordId = it.wordId!!
                list.add(wordId)
            }
        }
        return list
    }

    private fun searchWord(p0: String?) {
        if (wordAdapter.wOriginalList.isEmpty()) {
            when(p0) {
                "" -> no_words.text = getString(R.string.no_words)
                else -> no_words.text = getString(R.string.no_results_found, p0)
            }
            return
        }

        wordAdapter.filter.filter(HelperFunctions.toLowerCase(p0!!)) {
            isInitialized = true
            handler = Handler()
            recyclerViewScrollToPosition = Runnable { word_items.scrollToPosition(0) }
            when(it) {
                0 -> {
                    no_words.visibility = View.VISIBLE
                    no_words.text = getString(R.string.no_results_found, p0)
                }
                else -> no_words.visibility = View.GONE
            }
            handler.postDelayed(recyclerViewScrollToPosition, 300)
        }
    }

    private fun getWords() {
        swipe_to_refresh.isRefreshing = true
        wordAdapter = WordsAdapter(this)
        word_items.adapter = wordAdapter
        setUpTracker()
        viewModel.allWords.observe(viewLifecycleOwner, Observer { words ->
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
                swipe_to_refresh.isRefreshing = false
            }
            words.let { wordAdapter.submitList(it) }
            wordAdapter.setFilterWords(words)
            Timber.d("${words.size}")
        })
    }

    private fun setUpTracker() {
        tracker = SelectionTracker.Builder(
            MY_SELECTION,
            word_items,
            WordsItemKeyProvider(word_items),
            WordsItemDetailsLookup(word_items),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker!!.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                val items = tracker!!.selection.size()
                if (actionMode == null) {
                    actionMode = startActionMode()
                }
                when (items) {
                    0 -> actionMode?.finish()
                    else -> {
                        actionMode?.title = items.toString()
                        actionMode?.invalidate()
                    }
                }
            }
        })

        wordAdapter.tracker = tracker
        tracker?.onRestoreInstanceState(_savedInstanceState)

        // start action mode
        if (_savedInstanceState != null && _savedInstanceState!!.getBoolean(IS_IN_ACTION_MODE)) {
            actionMode = startActionMode()
            actionMode?.title = tracker?.selection?.size().toString()
        }
    }

    private fun resetTrackerAndActionMode() {
        isInActionMode = false
        tracker!!.clearSelection()
        actionMode = null
    }

    private fun openDeleteDialog(numberOfItems: Int) {
        val deleteDialog = DeleteDialog()
        val args = Bundle()
        args.putInt(NUMBER_OF_ITEMS, numberOfItems)
        deleteDialog.arguments = args
        deleteDialog.show(requireActivity().supportFragmentManager, DELETE_DIALOG)
    }

    private fun openDeleteDialogProgress(numberOfItems: Int, type: String) {
        val deleteDialogProgress = DeleteDialogProgress()
        val args = Bundle()
        args.putInt(NUMBER_OF_ITEMS_TO_DELETE, numberOfItems)
        typeOfDeletion = type
        deleteDialogProgress.arguments = args
        deleteDialogProgress.show(requireActivity().supportFragmentManager, DELETE_DIALOG_PROGRESS)
    }

    private fun startActionMode(): ActionMode? {
        return requireActivity().startActionMode(WordsActionModeCallback(this))
    }
}
