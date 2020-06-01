package com.fortie40.newword.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWord(wordModel: WordModel)

    @Query("SELECT * FROM words ORDER BY word_learned ASC")
    fun getAllWords(): LiveData<List<WordModel>>

    @Query("SELECT * FROM words WHERE wordId = :id")
    suspend fun getWordById(id: Int): WordModel

    @Query("UPDATE words SET word_learned = :wordLearned, language = :language, meaning = :meaning WHERE wordId = :id")
    suspend fun updateWord(wordLearned: String, language: String, meaning: String, id: Int)

    @Query("DELETE FROM words WHERE wordId = :id")
    suspend fun deleteWordById(id: Int)

    @Query("DELETE FROM words")
    suspend fun deleteAllWords()

    @Delete
    suspend fun deleteWord(wordModel: WordModel)
}