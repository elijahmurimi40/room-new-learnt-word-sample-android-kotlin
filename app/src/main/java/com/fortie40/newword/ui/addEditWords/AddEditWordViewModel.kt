package com.fortie40.newword.ui.addEditWords

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fortie40.newword.helperfunctions.HelperFunctions
import com.fortie40.newword.roomdatabase.WordModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditWordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AddEditWordRepository = AddEditWordRepository(application)

    var isSuccessfullyAdded = MutableLiveData<Boolean>(false)
    var isUpdatingWord = MutableLiveData<Boolean>(false)
    var isSuccessfullyUpdated = MutableLiveData<Boolean>(false)
    var wordId = MutableLiveData(0)

    var word = MutableLiveData<String>("")
    private var _isWordBlank = MutableLiveData<Boolean>(false)
    val isWordBlank: LiveData<Boolean> = _isWordBlank

    var language = MutableLiveData<String>("")
    private var _isLanguageBlank = MutableLiveData<Boolean>(false)
    val isLanguageBlank: LiveData<Boolean> = _isLanguageBlank

    var meaning = MutableLiveData<String>("")
    private var _isMeaningBlank = MutableLiveData<Boolean>(false)
    val isMeaningBlank: LiveData<Boolean> = _isMeaningBlank

    // save word
    private suspend fun saveWord(wordModel: WordModel) {
        repository.saveWord(wordModel)
    }

    // get word by id
    suspend fun getWord(id: Int): WordModel {
        return repository.getWord(id)
    }

    // update the word
    private suspend fun updateWord(wordLearned: String, language: String, meaning: String, id: Int) {
        repository.updateWord(wordLearned, language, meaning, id)
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
