package com.fortie40.newword.ui.addEditWord

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fortie40.newword.helperclasses.HelperFunctions
import com.fortie40.newword.roomdatabase.WordModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditWordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AddEditWordRepository = AddEditWordRepository(application)

    var isSuccessfullyAdded = MutableLiveData(false)
    var isUpdatingWord = MutableLiveData(false)
    var isSuccessfullyUpdated = MutableLiveData(false)
    var wordId = MutableLiveData(0)

    var word = MutableLiveData("")
    private var _isWordBlank = MutableLiveData(false)
    val isWordBlank: LiveData<Boolean> = _isWordBlank

    var language = MutableLiveData("")
    private var _isLanguageBlank = MutableLiveData(false)
    val isLanguageBlank: LiveData<Boolean> = _isLanguageBlank

    var meaning = MutableLiveData("")
    private var _isMeaningBlank = MutableLiveData(false)
    val isMeaningBlank: LiveData<Boolean> = _isMeaningBlank

    // save word
    private suspend fun saveWord(wordModel: WordModel) {
        repository.saveWord(wordModel)
    }

    // update the word
    private suspend fun updateWord(wordLearned: String, language: String, meaning: String, id: Int) {
        repository.updateWord(wordLearned, language, meaning, id)
    }

    // delete word
    suspend fun deleteWord(wordModel: WordModel) {
        repository.deleteWord(wordModel)
    }

    // save word on click
    fun saveWordOnClick() {
        if (validateWord() or validateLanguage() or validateMeaning()) {
            return
        } else {
            insertUpdateWord()
        }
    }

    // verify wordLearned
    private fun validateWord(): Boolean {
        _isWordBlank.value = word.value!!.trim().isEmpty()
        return isWordBlank.value!!
    }

    // verify language
    private fun validateLanguage(): Boolean {
        _isLanguageBlank.value = language.value!!.trim().isEmpty()
        return isLanguageBlank.value!!
    }

    // verify meaning
    private fun validateMeaning(): Boolean {
        _isMeaningBlank.value = meaning.value!!.trim().isEmpty()
        return isMeaningBlank.value!!
    }

    // Insert and update word
    private fun insertUpdateWord() {
        val wordModel = WordModel()
        val wordLearned = HelperFunctions.toLowerCase(word.value!!)
        val language = HelperFunctions.toLowerCase(language.value!!)
        val meaning = HelperFunctions.toLowerCase(meaning.value!!)

        wordModel.wordLearned = wordLearned
        wordModel.language = language
        wordModel.meaning = meaning

        if (isUpdatingWord.value!!) {
            CoroutineScope(IO).launch {
                updateWord(wordLearned, language, meaning, wordId.value!!)
                withContext(Main) {
                    isSuccessfullyUpdated.value = true
                }
            }
        } else {
            CoroutineScope(IO).launch {
                saveWord(wordModel)
                emptyInputs()
            }
        }
    }

    // empty inputs
    private suspend fun emptyInputs() {
        withContext(Main) {
            word.value = ""
            language.value = ""
            meaning.value = ""
            isSuccessfullyAdded.value = true
        }
    }
}
