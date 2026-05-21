package com.example.stemlab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ActivityListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        setupButton(R.id.btnParachute, "Parachute Drop Challenge")
        setupButton(R.id.btnSound, "Sound Pollution Hunter")
        setupButton(R.id.btnFan, "Hand Fan Challenge")
        setupButton(R.id.btnEarthquake, "Earthquake-Resistant Structure")
        setupButton(R.id.btnPerformance, "Human Performance Lab")
        setupButton(R.id.btnReaction, "Reaction Board Challenge")
        setupButton(R.id.btnBreathing, "Breathing Pace Trainer")

        val btnLeaderboard = findViewById<Button>(R.id.btnLeaderboard)

        btnLeaderboard.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupButton(buttonId: Int, activityName: String) {
        val button = findViewById<Button>(buttonId)

        button.setOnClickListener {
            val intent = Intent(this, ChallengeDetailActivity::class.java)
            intent.putExtra("challengeTitle", activityName)
            startActivity(intent)
        }
    }
}