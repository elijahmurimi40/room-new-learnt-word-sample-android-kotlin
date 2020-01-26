package com.fortie40.newword.roomdatabase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordRoomDatabaseTest {
    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var db: WordRoomDatabase
    private lateinit var wordDao: WordDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            WordRoomDatabase::class.java).allowMainThreadQueries().build()
        wordDao = db.wordDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndReadWord() = runBlocking {
        val words = listOf("elixir", "zopl", "kotlin", "angelScript", "smallTalk")
        for (word in words) {
            val wordModel = WordModel()
            val wordLanguage = "Programming languages"
            val wordMeaning = "Programming languages"

            wordModel.wordLearned = word
            wordModel.language = wordLanguage
            wordModel.meaning = wordMeaning
            wordDao.saveWord(wordModel)
        }

        val w = wordDao.getAllWords()
        w.observeForever {  }
        val v = w.value!!
        // words
        assertEquals(words[3], v[0].wordLearned)
        assertEquals(words[0], v[1].wordLearned)
        assertEquals(words[2], v[2].wordLearned)
        assertEquals(words[4], v[3].wordLearned)
        assertEquals(words[1], v[4].wordLearned)

        // id's
        assertEquals(4, v[0].wordId)
        assertEquals(1, v[1].wordId)
        assertEquals(3, v[2].wordId)
        assertEquals(5, v[3].wordId)
        assertEquals(2, v[4].wordId)
        w.removeObserver {  }
    }
}