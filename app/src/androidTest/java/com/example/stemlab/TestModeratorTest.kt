package com.example.stemlab

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import utils.TextModerator

class TextModeratorTest {

    @Test
    fun hasProfanity_withCleanText_returnsFalse() {
        assertFalse(TextModerator.hasProfanity("This is a clean sentence."))
    }

    @Test
    fun hasProfanity_withBannedWord_returnsTrue() {
        assertTrue(TextModerator.hasProfanity("This is a crap sentence."))
    }

    @Test
    fun hasProfanity_withLeetspeak_returnsTrue() {
        // '5' -> 's', '7' -> 't', '4' -> 'a'
        assertTrue(TextModerator.hasProfanity("This is a cr4p sentence."))
        assertTrue(TextModerator.hasProfanity("Don't be a 5tupid person."))
    }

    @Test
    fun hasProfanity_withUnicodeNormalization_returnsTrue() {
        assertTrue(TextModerator.hasProfanity("Don't be an 1d1ot"))
    }

    @Test
    fun sanitize_replacesBannedWordsWithAsterisks() {
        val input = "You are a stupid idiot"
        val expected = "You are a ****** *****"
        assertEquals(expected, TextModerator.sanitize(input))
    }

    @Test
    fun sanitize_preservesCleanText() {
        val input = "Hello world"
        assertEquals(input, TextModerator.sanitize(input))
    }

}