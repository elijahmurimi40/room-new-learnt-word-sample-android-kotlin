package com.fortie40.newword.interfaces

interface IClickListener {
    fun onWordClick(clickedItemIndex: Int)
    fun onWordLongClicked(clickedItemIndex: Int)
}