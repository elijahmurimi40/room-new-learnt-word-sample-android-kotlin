package com.fortie40.newword.roomdatabase

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.fortie40.newword.helperfunctions.HelperFunctions
import kotlin.random.Random

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

    @Ignore
    val wordLearnedC: String = HelperFunctions.capitalizeFirstLetter(wordLearned)

    @Ignore
    val languageC: String = HelperFunctions.capitalizeFirstLetter(language)

    @Ignore
    val meaningC: String = HelperFunctions.capitalizeFirstLetter(meaning)

    @Ignore
    val wordIcon: String = if (wordLearned != "") {
        HelperFunctions.capitalizeFirstLetter(wordLearned[0].toString())
    } else {
        "F"
    }

    @Ignore
    fun getRandomColor(): GradientDrawable {
        val r = Random
        val red = r.nextInt(150)
        val green = r.nextInt(150)
        val blue = r.nextInt(150)

        val draw = GradientDrawable()
        draw.shape = GradientDrawable.OVAL
        draw.setColor(Color.rgb(red, green, blue))
        return draw
    }

    @Ignore
    val iconColor = getRandomColor()
}