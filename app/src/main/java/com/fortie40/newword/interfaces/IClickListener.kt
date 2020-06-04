package com.fortie40.newword.interfaces

import com.fortie40.newword.roomdatabase.WordModel

interface IClickListener {
    fun onWordClick(wordModel: WordModel)

    fun onWordLongClick(id: Int)
}