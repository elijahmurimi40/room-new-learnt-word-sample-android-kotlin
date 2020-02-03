package com.fortie40.newword.ui.addEditWords

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.fortie40.newword.R
import com.fortie40.newword.databinding.AddEditWordFragmentBinding
import com.fortie40.newword.dialogs.DeleteDialog
import com.fortie40.newword.helperfunctions.HelperFunctions
import com.fortie40.newword.roomdatabase.WordModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_edit_word_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class AddEditWordFragment : Fragment() {

    companion object {
        private const val ID: String = "fortie40"
    }

    private lateinit var addEditWordFragmentBinding: AddEditWordFragmentBinding
    private lateinit var root: View
    private lateinit var viewModel: AddEditWordViewModel
    private lateinit var id: String
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
        id = AddEditWordFragmentArgs.fromBundle(arguments!!).id

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
            this.lifecycleOwner = this@AddEditWordFragment
            this.addEditWordViewModel = viewModel
        }

        // display word if available
        getWord(id)

        // onclick
        save_word.setOnClickListener {
            if (!validateWord() or !validateLanguage() or !validateMeaning()) {
                return@setOnClickListener
            }

            val wordModel = WordModel()
            wordModel.wordLearned = HelperFunctions.toLowerCase(viewModel.word.value!!)
            wordModel.language = HelperFunctions.toLowerCase(viewModel.language.value!!)
            wordModel.meaning = HelperFunctions.toLowerCase(viewModel.language.value!!)

            CoroutineScope(IO).launch {
                viewModel.saveWord(wordModel)
                showToast()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_edit_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.action_search).isVisible = false
        menu.findItem(R.id.action_delete).isVisible = id != ID
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete -> openDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun validateWord(): Boolean {
        return when {
            viewModel.word.value!!.trim().isEmpty() -> {
                word.error = getString(R.string.field_is_required)
                false
            }
            else -> {
                word.error = null
                true
            }
        }
    }

    private fun validateLanguage(): Boolean {
        return when {
            viewModel.language.value!!.trim().isEmpty() -> {
                language.error = getString(R.string.field_is_required)
                false
            }
            else -> {
                language.error = null
                true
            }
        }
    }

    private fun validateMeaning(): Boolean {
        return when {
            viewModel.meaning.value!!.trim().isEmpty() -> {
                meaning.error = getString(R.string.field_is_required)
                false
            }
            else -> {
                meaning.error = null
                true
            }
        }
    }

    private suspend fun showToast() {
        withContext(Main) {
            Toast.makeText(view!!.context, getString(R.string.successfully_added),
                Toast.LENGTH_SHORT).show()
            scroll_view.fullScroll(View.FOCUS_UP)
            viewModel.word.value = ""
            viewModel.language.value = ""
            viewModel.meaning.value = ""
        }
    }

    private fun getWord(id: String) {
        if (id == ID) {
            Timber.d(id)
        } else {
            Timber.d(id)
            val sum = id.toInt() + 1
            Timber.d("$sum")
        }
        activity?.invalidateOptionsMenu()
    }

    private fun openDialog() {
        val deleteDialog = DeleteDialog()
        deleteDialog.show(activity!!.supportFragmentManager, "Delete Dialog")
    }
}
