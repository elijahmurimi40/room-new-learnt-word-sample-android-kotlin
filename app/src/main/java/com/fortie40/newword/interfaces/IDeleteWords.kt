package com.fortie40.newword.interfaces

import android.view.View

interface IDeleteWords {
    suspend fun deleteWords(view: View, n: Int)
}