package com.example.stemlab

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class ReactionBoardActivity : AppCompatActivity() {

    private lateinit var handler: Handler

    private lateinit var etReactionNotes: EditText
    private lateinit var tvReactionStatus: TextView
    private lateinit var btnReactionTarget: Button
    private lateinit var tvReactionTime: TextView
    private lateinit var tvReactionStats: TextView
    private lateinit var tvReactionRating: TextView

    private var roundActive = false
    private var canTap = false
    private var targetAppearedTime = 0L

    private val reactionTimes = mutableListOf<Long>()

    private val showTargetRunnable = Runnable {
        if (roundActive) {
            canTap = true
            targetAppearedTime = System.currentTimeMillis()

            btnReactionTarget.text = "TAP NOW!"
            btnReactionTarget.isEnabled = true
            tvReactionStatus.text = "Tap the target now!"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reaction_board)

        handler = Handler(Looper.getMainLooper())

        etReactionNotes = findViewById(R.id.etReactionNotes)
        tvReactionStatus = findViewById(R.id.tvReactionStatus)
        btnReactionTarget = findViewById(R.id.btnReactionTarget)
        tvReactionTime = findViewById(R.id.tvReactionTime)
        tvReactionStats = findViewById(R.id.tvReactionStats)
        tvReactionRating = findViewById(R.id.tvReactionRating)

        val btnStartReactionRound = findViewById<Button>(R.id.btnStartReactionRound)
        val btnResetReaction = findViewById<Button>(R.id.btnResetReaction)
        val btnBackFromReaction = findViewById<Button>(R.id.btnBackFromReaction)

        btnStartReactionRound.setOnClickListener {
            startReactionRound()
        }

        btnReactionTarget.setOnClickListener {
            handleTargetTap()
        }

        btnResetReaction.setOnClickListener {
            resetReactionResults()
        }

        btnBackFromReaction.setOnClickListener {
            finish()
        }
    }

    private fun startReactionRound() {
        if (roundActive) {
            Toast.makeText(this, "Round already running.", Toast.LENGTH_SHORT).show()
            return
        }

        roundActive = true
        canTap = false
        targetAppearedTime = 0L

        btnReactionTarget.text = "WAIT..."
        btnReactionTarget.isEnabled = true
        tvReactionStatus.text = "Wait for the target. Do not tap early."

        val randomDelay = Random.nextLong(1500L, 4000L)
        handler.postDelayed(showTargetRunnable, randomDelay)
    }

    private fun handleTargetTap() {
        if (!roundActive) {
            return
        }

        if (!canTap) {
            handler.removeCallbacks(showTargetRunnable)
            roundActive = false
            canTap = false

            btnReactionTarget.text = "TOO EARLY"
            btnReactionTarget.isEnabled = false
            tvReactionStatus.text = "Too early! Press Start Round to try again."
            Toast.makeText(this, "Too early!", Toast.LENGTH_SHORT).show()
            return
        }

        val reactionTime = System.currentTimeMillis() - targetAppearedTime
        reactionTimes.add(reactionTime)

        roundActive = false
        canTap = false
        btnReactionTarget.text = "WAIT"
        btnReactionTarget.isEnabled = false

        updateReactionResults(reactionTime)
    }

    private fun updateReactionResults(lastReactionTime: Long) {
        val bestTime = reactionTimes.minOrNull() ?: lastReactionTime
        val averageTime = reactionTimes.average()
        val rating = getReactionRating(lastReactionTime)

        val notes = etReactionNotes.text.toString().trim()

        tvReactionStatus.text = "Round completed."
        tvReactionTime.text = "Last Reaction Time: ${lastReactionTime} ms"
        tvReactionStats.text = """
            Attempts: ${reactionTimes.size}
            Best Time: $bestTime ms
            Average Time: %.1f ms
            Notes: ${if (notes.isEmpty()) "Not recorded" else notes}
        """.trimIndent().format(averageTime)

        tvReactionRating.text = "Rating: $rating"
    }

    private fun resetReactionResults() {
        handler.removeCallbacks(showTargetRunnable)

        roundActive = false
        canTap = false
        targetAppearedTime = 0L
        reactionTimes.clear()

        btnReactionTarget.text = "WAIT"
        btnReactionTarget.isEnabled = false

        tvReactionStatus.text = "Press Start Round to begin."
        tvReactionTime.text = "Last Reaction Time: Not measured"
        tvReactionStats.text = "Attempts: 0"
        tvReactionRating.text = "Rating: Not measured"
    }

    private fun getReactionRating(reactionTime: Long): String {
        return when {
            reactionTime < 250 -> "Excellent reaction speed"
            reactionTime < 400 -> "Good reaction speed"
            reactionTime < 600 -> "Moderate reaction speed"
            else -> "Needs improvement"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(showTargetRunnable)
    }
}