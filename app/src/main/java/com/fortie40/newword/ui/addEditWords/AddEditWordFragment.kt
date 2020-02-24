package com.fortie40.newword.ui.addEditWords

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.fortie40.newword.R
import com.fortie40.newword.databinding.AddEditWordFragmentBinding
import com.fortie40.newword.dialogs.DeleteDialog
import com.fortie40.newword.helperfunctions.HelperFunctions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_edit_word_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditWordFragment : Fragment() {

    companion object {
        private const val WORD_ID: String = "fortie40"
    }

    private lateinit var addEditWordFragmentBinding: AddEditWordFragmentBinding
    private lateinit var root: View
    private lateinit var viewModel: AddEditWordViewModel
    private lateinit var wordId: String
    private lateinit var imm: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

        // change toolbar title
        activity!!.title = getString(R.string.add_word)
        // back button
        activity!!.toolbar.setNavigationIcon(R.drawable.back_button)
        activity!!.toolbar.setNavigationOnClickListener {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            activity!!.onBackPressed()
        }

        // get bundle
        wordId = AddEditWordFragmentArgs.fromBundle(arguments!!).wordId

        addEditWordFragmentBinding = AddEditWordFragmentBinding.inflate(inflater)
        root = addEditWordFragmentBinding.root
        setHasOptionsMenu(true)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //remove enter key on keyboard
        word_edit_text.imeOptions = EditorInfo.IME_ACTION_DONE
        word_edit_text.setRawInputType(InputType.TYPE_CLASS_TEXT)
        language_edit_text.imeOptions = EditorInfo.IME_ACTION_DONE
        language_edit_text.setRawInputType(InputType.TYPE_CLASS_TEXT)

        //close keyboard
        imm.hideSoftInputFromWindow(view?.windowToken, 0)

        viewModel = ViewModelProviders.of(this).get(AddEditWordViewModel::class.java)
        addEditWordFragmentBinding.apply {
            this.lifecycleOwner = viewLifecycleOwner
            this.addEditWordViewModel = viewModel
        }

        // show added toast
        viewModel.isSuccessfullyAdded.observe(viewLifecycleOwner, Observer {
            if (it) {
                HelperFunctions.showShortToast(view!!.context, getString(R.string.successfully_added))
                scroll_view.fullScroll(View.FOCUS_UP)
                viewModel.isSuccessfullyAdded.value = false
            }
        })

        // show toast for successfully updated
        viewModel.isSuccessfullyUpdated.observe(viewLifecycleOwner, Observer {
            if (it) {
                HelperFunctions.showShortToast(view!!.context, "Updated Successfully")
                viewModel.isSuccessfullyUpdated.value = false
            }
        })

        // display word if available
        getWord()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_edit_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_search).isVisible = false
        menu.findItem(R.id.action_delete).isVisible = wordId != WORD_ID
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> openDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getWord() {
        if (wordId == WORD_ID) {
            viewModel.isUpdatingWord.value = false
        } else {
            val wordIdInt = wordId.toInt()
            viewModel.wordId.value = wordIdInt
            viewModel.isUpdatingWord.value = true
            save_word.text = getString(R.string.update_word)
            activity!!.title = getString(R.string.word_edit)
            activity?.invalidateOptionsMenu()

            CoroutineScope(IO).launch {
                val wm = viewModel.getWord(wordIdInt)
                withContext(Main) {
                    viewModel.word.value = wm.wordLearnedC
                    viewModel.language.value = wm.languageC
                    viewModel.meaning.value = wm.meaningC
                }
            }
        }
    }

    private fun openDialog() {
        val deleteDialog = DeleteDialog(1)
        deleteDialog.show(activity!!.supportFragmentManager, "Delete Dialog")
    }
}
