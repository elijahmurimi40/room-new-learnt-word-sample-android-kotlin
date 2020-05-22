package com.fortie40.newword.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(WordModel::class)], version = 2, exportSchema = false)
abstract class WordRoomDatabase: RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: getDataBase(context).also {
                INSTANCE = it
            }
        }

        private fun getDataBase(context: Context) = Room.databaseBuilder(
            context.applicationContext, WordRoomDatabase::class.java,
            "words_database"
        ).fallbackToDestructiveMigration().build()
    }
}