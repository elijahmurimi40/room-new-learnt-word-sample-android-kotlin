package com.fortie40.newword.interfaces

interface IDeleteWords {
    suspend fun deleteWords(type: String)
}