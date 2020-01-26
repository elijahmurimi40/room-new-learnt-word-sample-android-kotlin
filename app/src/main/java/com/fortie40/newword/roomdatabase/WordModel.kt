package com.fortie40.newword.roomdatabase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class WordModel(
    @ColumnInfo(name = "word_learned")
    var wordLearned: String = "",

    @ColumnInfo(name = "language")
    var language: String = "",

    @ColumnInfo(name = "meaning")
    var meaning: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var wordId: Int = 0
}