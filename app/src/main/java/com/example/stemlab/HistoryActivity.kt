package com.example.stemlab

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val tvHistory = findViewById<TextView>(R.id.tvHistory)

        val sharedPreferences = getSharedPreferences("STEMM_RESULTS", MODE_PRIVATE)
        val history = sharedPreferences.getString("history", "")

        if (history.isNullOrBlank()) {
            tvHistory.text = "No results submitted yet."
        } else {
            tvHistory.text = history
        }
    }
}