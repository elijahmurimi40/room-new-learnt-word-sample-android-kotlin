package com.fortie40.newword.ui.words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.fortie40.newword.roomdatabase.WordModel

class WordsViewModel(application: Application) : AndroidViewModel(application) {
    private val wordsRepository: WordsRepository = WordsRepository(application)
    val allProducts: LiveData<List<WordModel>>

    init {
        allProducts = wordsRepository.getAllWords()
    }
}
