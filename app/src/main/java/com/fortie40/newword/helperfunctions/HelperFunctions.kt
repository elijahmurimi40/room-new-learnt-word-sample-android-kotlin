package com.fortie40.newword.helperfunctions

import java.util.*

object HelperFunctions {
    // all characters to lowercase
    fun toLowerCase(str: String): String {
        if (str.isEmpty()) {
            return str
        }

        return str.toLowerCase(Locale.getDefault())
    }

    // first letter of any string to uppercase
    fun capitalizeFirstLetter(str: String): String {
        if (str.isEmpty()) {
            return str
        }

        val strToLower = toLowerCase(str)
        return strToLower.substring(0, 1).toUpperCase(Locale.getDefault()) + strToLower.substring(1)
    }
}