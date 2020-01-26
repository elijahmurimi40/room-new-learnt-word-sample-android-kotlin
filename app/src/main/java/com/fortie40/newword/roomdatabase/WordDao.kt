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
}