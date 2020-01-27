package com.fortie40.newword.ui.addEditWords

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.fortie40.newword.R
import com.fortie40.newword.databinding.AddEditWordFragmentBinding
import kotlinx.android.synthetic.main.activity_main.*

class AddEditWordFragment : Fragment() {

    private lateinit var addEditWordFragmentBinding: AddEditWordFragmentBinding
    private lateinit var root: View
    private lateinit var viewModel: AddEditWordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // change toolbar title
        activity!!.title = getString(R.string.add_word)
        // back button
        activity!!.toolbar.setNavigationIcon(R.drawable.back_button)
        activity!!.toolbar.setNavigationOnClickListener {
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            activity!!.onBackPressed()
        }

        addEditWordFragmentBinding = AddEditWordFragmentBinding.inflate(inflater)
        root = addEditWordFragmentBinding.root
        setHasOptionsMenu(true)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddEditWordViewModel::class.java)
        addEditWordFragmentBinding.apply {
            this.lifecycleOwner = this@AddEditWordFragment
            this.addEditWordViewModel = viewModel
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_settings).isVisible = false
    }
}
