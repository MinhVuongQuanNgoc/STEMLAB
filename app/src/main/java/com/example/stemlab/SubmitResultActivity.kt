package com.example.stemlab

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stemlab.utils.ScoreCalculator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SubmitResultActivity : AppCompatActivity() {

    private var latitude: Double? = null
    private var longitude: Double? = null
    private var elapsedSeconds: Double = 0.0

    private var timerStartTime: Long = 0L
    private var isTimerRunning = false
    private lateinit var timerHandler: Handler

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLocation: TextView

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_result)

        val btnBackFromSubmit = findViewById<android.widget.ImageButton>(R.id.btnBackFromSubmit)
        btnBackFromSubmit.setOnClickListener {
            finish()
        }

        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val challengeTitle = intent.getStringExtra("challengeTitle") ?: "STEMM Challenge"

        val tvSubmitTitle = findViewById<TextView>(R.id.tvSubmitTitle)
        val etPrediction = findViewById<EditText>(R.id.etPrediction)
        val etResult = findViewById<EditText>(R.id.etResult)
        val etReflection = findViewById<EditText>(R.id.etReflection)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val btnSubmitResult = findViewById<Button>(R.id.btnSubmitResult)
        val btnViewHistory = findViewById<Button>(R.id.btnViewHistory)
        val tvSubmittedResult = findViewById<TextView>(R.id.tvSubmittedResult)

        val tvTimer = findViewById<TextView>(R.id.tvTimer)
        val btnStartTimer = findViewById<Button>(R.id.btnStartTimer)
        val btnStopTimer = findViewById<Button>(R.id.btnStopTimer)
        val btnGetLocation = findViewById<Button>(R.id.btnGetLocation)
        tvLocation = findViewById(R.id.tvLocation)

        timerHandler = Handler(Looper.getMainLooper())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tvSubmitTitle.text = "Submit Result: $challengeTitle"

        val timerRunnable = object : Runnable {
            override fun run() {
                if (isTimerRunning) {
                    elapsedSeconds = (System.currentTimeMillis() - timerStartTime) / 1000.0
                    tvTimer.text = "Timer: %.1f seconds".format(elapsedSeconds)
                    timerHandler.postDelayed(this, 100)
                }
            }
        }

        btnStartTimer.setOnClickListener {
            timerStartTime = System.currentTimeMillis()
            elapsedSeconds = 0.0
            isTimerRunning = true
            timerHandler.post(timerRunnable)
        }

        btnStopTimer.setOnClickListener {
            isTimerRunning = false
            tvTimer.text = "Timer: %.1f seconds".format(elapsedSeconds)
        }

        btnGetLocation.setOnClickListener {
            if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                return@setOnClickListener
            }

            getCurrentLocation()
        }

        btnSubmitResult.setOnClickListener {
            val prediction = etPrediction.text.toString().trim()
            val result = etResult.text.toString().trim()
            val reflection = etReflection.text.toString().trim()
            val rating = ratingBar.rating.toInt()

            if (prediction.isEmpty() || result.isEmpty() || reflection.isEmpty()) {
                Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formattedElapsed = "%.1f".format(elapsedSeconds)
            val latitudeText = latitude?.toString() ?: "Not recorded"
            val longitudeText = longitude?.toString() ?: "Not recorded"

            val newRecord = """
                Challenge: $challengeTitle
                Prediction: $prediction
                Result: $result
                Reflection: $reflection
                Rating: $rating / 5
                Time: $formattedElapsed seconds
                Latitude: $latitudeText
                Longitude: $longitudeText
                ------------------------------
                
            """.trimIndent()

            val sharedPreferences = getSharedPreferences("STEMM_RESULTS", MODE_PRIVATE)
            val oldHistory = sharedPreferences.getString("history", "") ?: ""

            sharedPreferences.edit()
                .putString("history", oldHistory + newRecord)
                .apply()

            val teamPreferences = getSharedPreferences("STEMM_TEAM", MODE_PRIVATE)
            val teamName = teamPreferences.getString("teamName", "Unknown Team") ?: "Unknown Team"

            val score = ScoreCalculator.calculateScore(rating)

            val leaderboardRecord = """
                Team: $teamName
                Challenge: $challengeTitle
                Score: $score
                Rating: $rating / 5
                ------------------------------
                
            """.trimIndent()

            val oldLeaderboard = sharedPreferences.getString("leaderboard", "") ?: ""

            sharedPreferences.edit()
                .putString("leaderboard", oldLeaderboard + leaderboardRecord)
                .apply()

            tvSubmittedResult.text = """
                Result Submitted Successfully!
                
                Challenge: $challengeTitle
                Prediction: $prediction
                Result: $result
                Reflection: $reflection
                Rating: $rating / 5
                Time: $formattedElapsed seconds
                Latitude: $latitudeText
                Longitude: $longitudeText
            """.trimIndent()

            val resultData = hashMapOf(
                "userId" to (auth.currentUser?.uid ?: "unknown"),
                "teamName" to teamName,
                "challengeTitle" to challengeTitle,
                "prediction" to prediction,
                "result" to result,
                "reflection" to reflection,
                "rating" to rating,
                "score" to score,
                "elapsedSeconds" to elapsedSeconds,
                "latitude" to latitude,
                "longitude" to longitude,
                "timestamp" to System.currentTimeMillis()
            )

            firestore.collection("results")
                .add(resultData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Result saved to Firestore.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Firestore save failed: ${exception.message}", Toast.LENGTH_LONG).show()
                }

            Toast.makeText(this, "Result saved to history.", Toast.LENGTH_SHORT).show()
        }

        btnViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude

                tvLocation.text = """
                    Location:
                    Latitude: $latitude
                    Longitude: $longitude
                """.trimIndent()

                Toast.makeText(this, "Location recorded.", Toast.LENGTH_SHORT).show()
            } else {
                tvLocation.text = "Location: Not available. Set emulator location and try again."
                Toast.makeText(this, "Location not available yet.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Location failed: ${exception.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (
            requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacksAndMessages(null)
    }
}