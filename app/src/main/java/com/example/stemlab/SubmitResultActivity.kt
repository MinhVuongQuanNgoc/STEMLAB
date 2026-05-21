package com.example.stemlab

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SubmitResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_result)

        val challengeTitle = intent.getStringExtra("challengeTitle") ?: "STEMM Challenge"

        val tvSubmitTitle = findViewById<TextView>(R.id.tvSubmitTitle)
        val etPrediction = findViewById<EditText>(R.id.etPrediction)
        val etResult = findViewById<EditText>(R.id.etResult)
        val etReflection = findViewById<EditText>(R.id.etReflection)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val btnSubmitResult = findViewById<Button>(R.id.btnSubmitResult)
        val tvSubmittedResult = findViewById<TextView>(R.id.tvSubmittedResult)

        tvSubmitTitle.text = "Submit Result: $challengeTitle"

        btnSubmitResult.setOnClickListener {
            val prediction = etPrediction.text.toString().trim()
            val result = etResult.text.toString().trim()
            val reflection = etReflection.text.toString().trim()
            val rating = ratingBar.rating.toInt()

            if (prediction.isEmpty() || result.isEmpty() || reflection.isEmpty()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            tvSubmittedResult.text = """
                Result Submitted Successfully!
                
                Challenge: $challengeTitle
                Prediction: $prediction
                Result: $result
                Reflection: $reflection
                Rating: $rating / 5
            """.trimIndent()
        }
    }
}