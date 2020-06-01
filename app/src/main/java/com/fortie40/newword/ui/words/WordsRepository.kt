package com.fortie40.newword.ui.words

import android.app.Application
import androidx.lifecycle.LiveData
import com.fortie40.newword.roomdatabase.WordDao
import com.fortie40.newword.roomdatabase.WordModel
import com.fortie40.newword.roomdatabase.WordRoomDatabase

class WordsRepository() {
    private lateinit var wordDao: WordDao
    private lateinit var allWords: LiveData<List<WordModel>>

    constructor(application: Application): this() {
        val database = WordRoomDatabase(application)
        wordDao = database.wordDao()
        allWords = wordDao.getAllWords()
    }

    fun getAllWords(): LiveData<List<WordModel>> {
        return allWords
    }

    suspend fun deleteWordById(id: Int) {
        wordDao.deleteWordById(id)
    }
}