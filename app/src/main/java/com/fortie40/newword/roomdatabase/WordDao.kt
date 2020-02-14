package com.fortie40.newword.roomdatabase

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWord(wordModel: WordModel)

    @Query("SELECT * FROM words ORDER BY word_learned ASC")
    fun getAllWords(): LiveData<List<WordModel>>

    @Query("SELECT * FROM words WHERE wordId = :id")
    suspend fun getWord(id: Int): WordModel

    @Query("DELETE FROM words WHERE wordId = :id")
    suspend fun deleteWord(id: Int)

    @Query("UPDATE words SET word_learned = :wordLearned, language = :language, meaning = :meaning WHERE wordId = :id")
    suspend fun updateWord(wordLearned: String, language: String, meaning: String, id: Int)
}