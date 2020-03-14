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
import com.fortie40.newword.DELETE_DIALOG
import com.fortie40.newword.R
import com.fortie40.newword.databinding.AddEditWordFragmentBinding
import com.fortie40.newword.dialogs.DeleteDialog
import com.fortie40.newword.helperclasses.HelperFunctions
import com.fortie40.newword.interfaces.IDeleteDialogListener
import com.fortie40.newword.roomdatabase.WordModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_edit_word_fragment.*

class AddEditWordFragment : Fragment(), IDeleteDialogListener {

    private lateinit var addEditWordFragmentBinding: AddEditWordFragmentBinding
    private lateinit var root: View
    private lateinit var viewModel: AddEditWordViewModel
    private lateinit var imm: InputMethodManager

    private var wordModel: WordModel? = null

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

        // get wordModel
        wordModel = AddEditWordFragmentArgs.fromBundle(arguments!!).wordM
        getWord()

        // show added toast
        viewModel.isSuccessfullyAdded.observe(viewLifecycleOwner, Observer {
            if (it) {
                HelperFunctions.showShortSnackBar(
                    view!!,
                    getString(R.string.successfully_added)
                )
                scroll_view.fullScroll(View.FOCUS_UP)
                viewModel.isSuccessfullyAdded.value = false
            }
        })

        // show toast for successfully updated
        viewModel.isSuccessfullyUpdated.observe(viewLifecycleOwner, Observer {
            if (it) {
                HelperFunctions.showShortSnackBar(
                    view!!,
                    getString(R.string.Updated_successfully)
                )
                viewModel.isSuccessfullyUpdated.value = false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_edit_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_search).isVisible = false
        menu.findItem(R.id.delete_all_words).isVisible = false
        menu.findItem(R.id.action_delete).isVisible = wordModel != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> openDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        DeleteDialog.deleteDialogListener = this
    }

    override fun onStop() {
        DeleteDialog.deleteDialogListener = null
        super.onStop()
    }

    override fun onDeletePressed() {
        HelperFunctions.showShortSnackBar(view!!, getString(R.string.successfully_deleted))
        activity!!.onBackPressed()
    }

    private fun getWord() {
        if (wordModel == null) {
            viewModel.isUpdatingWord.value = false
        } else {
            viewModel.isUpdatingWord.value = true
            viewModel.wordId.value = wordModel!!.wordId
            save_word.text = getString(R.string.update_word)
            activity!!.title = getString(R.string.word_edit)
            activity?.invalidateOptionsMenu()

            viewModel.word.value = wordModel!!.wordLearnedC
            viewModel.language.value = wordModel!!.languageC
            viewModel.meaning.value = wordModel!!.meaningC
        }
    }

    private fun openDialog() {
        val deleteDialog = DeleteDialog()
        deleteDialog.show(activity!!.supportFragmentManager, DELETE_DIALOG)
    }
}
