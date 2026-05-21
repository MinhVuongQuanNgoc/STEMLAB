package com.example.stemlab

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val tvLeaderboard = findViewById<TextView>(R.id.tvLeaderboard)

        val sharedPreferences = getSharedPreferences("STEMM_RESULTS", MODE_PRIVATE)
        val leaderboard = sharedPreferences.getString("leaderboard", "")

        if (leaderboard.isNullOrBlank()) {
            tvLeaderboard.text = "No leaderboard results yet."
        } else {
            tvLeaderboard.text = leaderboard
        }
    }
}