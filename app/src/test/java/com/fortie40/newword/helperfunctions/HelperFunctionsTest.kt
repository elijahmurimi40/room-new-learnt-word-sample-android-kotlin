package com.fortie40.newword.helperfunctions

import org.junit.Assert.*
import org.junit.Test

class HelperFunctionsTest {

    @Test
    fun toLowerCase() {
        val str1 = "A cat HAS A rAt"
        val str2 = "THE QUICK broWn Fox Jumped over THE lazy DoGs"
        val str3 = "fortIE40"
        val str4 = "200KG"
        val str5 = "5000 kg"
        assertEquals("a cat has a rat", HelperFunctions.toLowerCase(str1))
        assertEquals("the quick brown fox jumped over the lazy dogs", HelperFunctions.toLowerCase(str2))
        assertEquals("fortie40", HelperFunctions.toLowerCase(str3))
        assertEquals("200kg", HelperFunctions.toLowerCase(str4))
        assertEquals("5000 kg", HelperFunctions.toLowerCase(str5))
    }

    @Test
    fun capitalizeFirstWord() {
        val str1 = "A cat HAS A rAt"
        val str2 = "THE QUICK broWn Fox Jumped over THE lazy DoGs"
        val str3 = "fortIE40"
        val str4 = "200KG"
        val str5 = "5000 kg"
        assertEquals("A cat has a rat", HelperFunctions.capitalizeFirstWord(str1))
        assertEquals("The quick brown fox jumped over the lazy dogs", HelperFunctions.capitalizeFirstWord(str2))
        assertEquals("Fortie40", HelperFunctions.capitalizeFirstWord(str3))
        assertEquals("200kg", HelperFunctions.capitalizeFirstWord(str4))
        assertEquals("5000 kg", HelperFunctions.capitalizeFirstWord(str5))
    }
}