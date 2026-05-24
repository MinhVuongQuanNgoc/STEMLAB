package com.example.stemlab

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val tvLeaderboard = findViewById<TextView>(R.id.tvLeaderboard)
        val firestore = FirebaseFirestore.getInstance()

        tvLeaderboard.text = "Loading leaderboard..."

        firestore.collection("results")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    tvLeaderboard.text = "No leaderboard results yet."
                    return@addOnSuccessListener
                }

                val leaderboardText = StringBuilder()
                var rank = 1

                for (document in documents) {
                    val teamName = document.getString("teamName") ?: "Unknown Team"
                    val challengeTitle = document.getString("challengeTitle") ?: "Unknown Challenge"
                    val score = document.getLong("score") ?: 0
                    val rating = document.getLong("rating") ?: 0

                    leaderboardText.append(
                        """
                        #$rank
                        Team: $teamName
                        Challenge: $challengeTitle
                        Score: $score
                        Rating: $rating / 5
                        ------------------------------
                        
                        """.trimIndent()
                    )

                    rank++
                }

                tvLeaderboard.text = leaderboardText.toString()
            }
            .addOnFailureListener { exception ->
                tvLeaderboard.text = "Failed to load leaderboard."
                Toast.makeText(
                    this,
                    "Leaderboard error: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}