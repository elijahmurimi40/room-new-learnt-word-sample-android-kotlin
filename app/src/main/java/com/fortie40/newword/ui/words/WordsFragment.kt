package com.fortie40.newword.ui.words

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.fortie40.newword.R
import com.fortie40.newword.databinding.WordsFragmentBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.words_fragment.*
import timber.log.Timber

class WordsFragment : Fragment() {

    private lateinit var wordsFragmentBinding: WordsFragmentBinding
    private lateinit var root: View
    private lateinit var viewModel: WordsViewModel

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

    private fun getWords() {
        swipe_to_refresh.isRefreshing = true
        viewModel.allProducts.observe(viewLifecycleOwner, Observer { words ->
            if (words.isEmpty()) {
                swipe_to_refresh.isRefreshing = false
            } else {
                for (word in words) {
                    Timber.d("***************************************************************")
                    Timber.d("ID: ${word.wordId}")
                    Timber.d("Word: ${word.wordLearned}")
                    Timber.d("***************************************************************")
                }
                swipe_to_refresh.isRefreshing = false
            }
        })
    }

}
