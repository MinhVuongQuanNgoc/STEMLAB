package com.example.stemlab.utils

object ScoreCalculator {

    fun calculateScore(rating: Int): Int {
        val safeRating = rating.coerceIn(0, 5)
        return safeRating * 20
    }
}