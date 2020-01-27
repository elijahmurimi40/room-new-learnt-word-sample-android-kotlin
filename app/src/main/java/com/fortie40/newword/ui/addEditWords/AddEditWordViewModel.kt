package com.fortie40.newword.ui.addEditWords

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddEditWordViewModel : ViewModel() {
    var word = MutableLiveData<String>("")
    var language = MutableLiveData<String>("")
    var meaning = MutableLiveData<String>("")
}
