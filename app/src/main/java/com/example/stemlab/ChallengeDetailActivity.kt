package com.example.stemlab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ChallengeDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_challenge_detail)

        val title = intent.getStringExtra("challengeTitle") ?: "STEMM Challenge"

        val tvTitle = findViewById<TextView>(R.id.tvChallengeTitle)
        val tvCategory = findViewById<TextView>(R.id.tvChallengeCategory)
        val tvOverview = findViewById<TextView>(R.id.tvChallengeOverview)
        val tvInstructions = findViewById<TextView>(R.id.tvChallengeInstructions)
        val btnStartChallenge = findViewById<Button>(R.id.btnStartChallenge)
        val btnOpenSensor = findViewById<Button>(R.id.btnOpenSensor)

        tvTitle.text = title

        when (title) {
            "Parachute Drop Challenge" -> {
                tvCategory.text = "Engineering + Physics"
                tvOverview.text = "Design, build, and test a parachute to reduce the landing speed and impact force of a small toy."
                tvInstructions.text = """
                    1. Drop the toy without a parachute.
                    2. Record the baseline test.
                    3. Build a parachute using paper, plastic, string, and tape.
                    4. Drop the toy again from the same height.
                    5. Compare the fall time and landing result.
                """.trimIndent()
            }

            "Sound Pollution Hunter" -> {
                tvCategory.text = "Environmental Science"
                tvOverview.text = "Measure and compare sound levels from different classroom actions."
                tvInstructions.text = """
                    1. Choose several classroom actions.
                    2. Record or enter the sound level.
                    3. Compare loud and quiet zones.
                    4. Reflect on possible health effects of loud noise.
                """.trimIndent()
            }

            "Reaction Board Challenge" -> {
                tvCategory.text = "Neuroscience + Mathematics"
                tvOverview.text = "Measure reaction time and compare performance between dominant and non-dominant hands."
                tvInstructions.text = """
                    1. Tap the screen when the button appears.
                    2. Record reaction time.
                    3. Repeat with the other hand.
                    4. Compare results and improvement.
                """.trimIndent()
            }

            else -> {
                tvCategory.text = "STEMM Activity"
                tvOverview.text = "Complete this real-world STEMM challenge and record your prediction, result, and reflection."
                tvInstructions.text = """
                    1. Read the activity instructions.
                    2. Make a prediction.
                    3. Complete the challenge.
                    4. Record the result.
                    5. Reflect on what happened.
                """.trimIndent()
            }
        }

        btnStartChallenge.setOnClickListener {
            val intent = Intent(this, SubmitResultActivity::class.java)
            intent.putExtra("challengeTitle", title)
            startActivity(intent)
        }

        btnOpenSensor.setOnClickListener {
            val intent = Intent(this, SensorActivity::class.java)
            intent.putExtra("challengeTitle", title)
            startActivity(intent)
        }
    }
}