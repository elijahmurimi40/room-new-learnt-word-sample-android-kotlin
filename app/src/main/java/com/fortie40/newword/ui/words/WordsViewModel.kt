package com.fortie40.newword.ui.words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.fortie40.newword.roomdatabase.WordModel
import kotlinx.coroutines.launch

class WordsViewModel(application: Application) : AndroidViewModel(application) {
    private val wordsRepository: WordsRepository = WordsRepository(application)
    val allWords: LiveData<List<WordModel>>

    init {
        allWords = wordsRepository.getAllWords()
    }

    fun deleteAllWords() {
        viewModelScope.launch {
            wordsRepository.deleteAllWords()
        }
    }
}
