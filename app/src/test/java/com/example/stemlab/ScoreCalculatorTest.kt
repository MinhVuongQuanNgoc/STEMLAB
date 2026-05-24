package com.example.stemlab

import com.example.stemlab.utils.ScoreCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreCalculatorTest {

    @Test
    fun calculateScore_ratingFive_returnsOneHundred() {
        val result = ScoreCalculator.calculateScore(5)
        assertEquals(100, result)
    }

    @Test
    fun calculateScore_ratingFour_returnsEighty() {
        val result = ScoreCalculator.calculateScore(4)
        assertEquals(80, result)
    }

    @Test
    fun calculateScore_ratingZero_returnsZero() {
        val result = ScoreCalculator.calculateScore(0)
        assertEquals(0, result)
    }

    @Test
    fun calculateScore_ratingAboveFive_returnsOneHundred() {
        val result = ScoreCalculator.calculateScore(10)
        assertEquals(100, result)
    }

    @Test
    fun calculateScore_ratingBelowZero_returnsZero() {
        val result = ScoreCalculator.calculateScore(-1)
        assertEquals(0, result)
    }
}