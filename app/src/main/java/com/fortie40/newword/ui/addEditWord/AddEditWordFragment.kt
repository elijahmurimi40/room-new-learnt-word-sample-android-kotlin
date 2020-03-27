package com.fortie40.newword.ui.addEditWord

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

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
        imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager

        // change toolbar title
        requireActivity().title = getString(R.string.add_word)
        // back button
        requireActivity().toolbar.setNavigationIcon(R.drawable.back_button)
        requireActivity().toolbar.setNavigationOnClickListener {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            requireActivity().onBackPressed()
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
        wordModel = AddEditWordFragmentArgs.fromBundle(requireArguments()).wordM
        getWord()

        // show added toast
        viewModel.isSuccessfullyAdded.observe(viewLifecycleOwner, Observer {
            if (it) {
                HelperFunctions.showShortSnackBar(
                    requireView(),
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
                    requireView(),
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
            R.id.action_delete -> openDeleteDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        DeleteDialog.deleteDialogListener = this
    }

    override fun onDeletePressed() {
        CoroutineScope(IO).launch {
            viewModel.deleteWord(wordModel!!)
        }
        HelperFunctions.showShortSnackBar(requireView(), getString(R.string.successfully_deleted))
        requireActivity().onBackPressed()
    }

    private fun getWord() {
        if (wordModel == null) {
            viewModel.isUpdatingWord.value = false
        } else {
            viewModel.isUpdatingWord.value = true
            viewModel.wordId.value = wordModel!!.wordId
            save_word.text = getString(R.string.update_word)
            requireActivity().title = getString(R.string.word_edit)
            activity?.invalidateOptionsMenu()

            viewModel.word.value = wordModel!!.wordLearnedC
            viewModel.language.value = wordModel!!.languageC
            viewModel.meaning.value = wordModel!!.meaningC
        }
    }

    private fun openDeleteDialog() {
        val deleteDialog = DeleteDialog()
        deleteDialog.show(requireActivity().supportFragmentManager, DELETE_DIALOG)
    }
}
