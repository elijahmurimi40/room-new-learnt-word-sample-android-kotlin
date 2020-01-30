package com.fortie40.newword.ui.words

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fortie40.newword.R
import com.fortie40.newword.databinding.WordsFragmentBinding
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
        swipe_to_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary,
            R.color.colorPrimaryDark)
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

    override fun viewDetails(clickedItemIndex: Int) {
        val wordAtPosition = allWords?.get(clickedItemIndex)
        Timber.d("${wordAtPosition?.wordLearned}")

        val action =
            WordsFragmentDirections.actionWordsFragmentToAddEditWordFragment()
        action.id = wordAtPosition!!.wordId.toString()
        activity?.findNavController(R.id.nav_host_fragment)?.navigate(action)
    }

}
