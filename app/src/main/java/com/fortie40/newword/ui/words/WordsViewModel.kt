package com.fortie40.newword.ui.words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.selection.Selection
import com.fortie40.newword.DELETE_ALL_WORDS
import com.fortie40.newword.DELETE_ICON_PRESSED
import com.fortie40.newword.roomdatabase.WordModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class WordsViewModel(application: Application) : AndroidViewModel(application) {
    private val wordsRepository: WordsRepository = WordsRepository(application)
    val allWords: LiveData<List<WordModel>>

    init {
        allWords = wordsRepository.getAllWords()
    }

    var oneWordId: Int = 0

    private var _progress = MutableLiveData<Int>()
    var progress: LiveData<Int> = _progress

    private var _i = MutableLiveData<Int>()
    var i: LiveData<Int> = _i

    private fun deleteWordById(id: Int) {
        CoroutineScope(IO).launch {
            wordsRepository.deleteWordById(id)
        }
    }

    fun deleteWordProgress(n: Int, type: String, selection: Selection<Long>?) = runBlocking {
        delay(200)
        val wordIdList = getWordIdList(type, selection)
        for (i in 1..n) {
            val wordId = wordIdList[i - 1]
            deleteWordById(wordId)
            withContext(Main) {
                _progress.value = ((i.toFloat() / n) * 100).toInt()
                this@WordsViewModel._i.value = i
            }
            delay(500)
        }
        delay(300)
    }

    private fun getWordIdList(type: String, selection: Selection<Long>?): List<Int> {
        val wm = allWords.value
        var wordId: Int
        val list = ArrayList<Int>()

        when (type) {
            DELETE_ICON_PRESSED -> {
                selection!!.map {
                    wordId = wm!![it.toInt()].wordId!!
                    list.add(wordId)
                }
            }
            DELETE_ALL_WORDS -> {
                wm!!.map {
                    wordId = it.wordId!!
                    list.add(wordId)
                }
            }
            else -> {
                list.add(oneWordId)
            }
        }
        return list
    }
}
