package com.example.stemlab

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.math.sqrt

class HumanPerformanceActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var etMovementNotes: EditText
    private lateinit var tvCurrentMovement: TextView
    private lateinit var tvPeakMovement: TextView
    private lateinit var tvSmoothness: TextView
    private lateinit var tvElapsedMovementTime: TextView
    private lateinit var tvHumanPerformanceResult: TextView

    private var isTesting = false
    private var peakMovement = 0.0
    private var lastMovementStrength: Double? = null
    private var startTime = 0L

    private val movementSamples = mutableListOf<Double>()
    private val movementChanges = mutableListOf<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_human_performance)

        etMovementNotes = findViewById(R.id.etMovementNotes)
        tvCurrentMovement = findViewById(R.id.tvCurrentMovement)
        tvPeakMovement = findViewById(R.id.tvPeakMovement)
        tvSmoothness = findViewById(R.id.tvSmoothness)
        tvElapsedMovementTime = findViewById(R.id.tvElapsedMovementTime)
        tvHumanPerformanceResult = findViewById(R.id.tvHumanPerformanceResult)

        val btnStartHumanPerformance = findViewById<Button>(R.id.btnStartHumanPerformance)
        val btnStopHumanPerformance = findViewById<Button>(R.id.btnStopHumanPerformance)
        val btnResetHumanPerformance = findViewById<Button>(R.id.btnResetHumanPerformance)
        val btnBackFromHumanPerformance = findViewById<android.widget.ImageButton>(R.id.btnBackFromHumanPerformance)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Toast.makeText(this, "Accelerometer sensor not available.", Toast.LENGTH_LONG).show()
        }

        btnStartHumanPerformance.setOnClickListener {
            resetTestData()
            startTime = System.currentTimeMillis()
            isTesting = true
            tvHumanPerformanceResult.text = "Movement test started. Perform a controlled stretch."
            Toast.makeText(this, "Movement test started.", Toast.LENGTH_SHORT).show()
        }

        btnStopHumanPerformance.setOnClickListener {
            stopAndShowResult()
        }

        btnResetHumanPerformance.setOnClickListener {
            resetTestData()
            tvHumanPerformanceResult.text = "Result reset."
        }

        btnBackFromHumanPerformance.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isTesting) return

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val movementStrength = sqrt((x * x + y * y + z * z).toDouble())
            val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0

            movementSamples.add(movementStrength)

            lastMovementStrength?.let { previous ->
                val change = abs(movementStrength - previous)
                movementChanges.add(change)
            }

            lastMovementStrength = movementStrength

            if (movementStrength > peakMovement) {
                peakMovement = movementStrength
            }

            val averageChange = if (movementChanges.isNotEmpty()) {
                movementChanges.average()
            } else {
                0.0
            }

            tvCurrentMovement.text = "Current Movement: %.2f".format(movementStrength)
            tvPeakMovement.text = "Peak Movement: %.2f".format(peakMovement)
            tvSmoothness.text = "Smoothness: ${getSmoothnessRating(averageChange)}"
            tvElapsedMovementTime.text = "Elapsed Time: %.1f seconds".format(elapsedSeconds)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed for this prototype.
    }

    private fun stopAndShowResult() {
        if (!isTesting) {
            Toast.makeText(this, "No active movement test.", Toast.LENGTH_SHORT).show()
            return
        }

        isTesting = false

        val notes = etMovementNotes.text.toString().trim()
        val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0

        val averageMovement = if (movementSamples.isNotEmpty()) {
            movementSamples.average()
        } else {
            0.0
        }

        val averageChange = if (movementChanges.isNotEmpty()) {
            movementChanges.average()
        } else {
            0.0
        }

        val smoothnessRating = getSmoothnessRating(averageChange)
        val performanceRating = getPerformanceRating(peakMovement, averageChange)

        tvHumanPerformanceResult.text = """
            Human Performance Result
            
            Movement Notes: ${if (notes.isEmpty()) "Not recorded" else notes}
            Elapsed Time: %.1f seconds
            Average Movement: %.2f
            Peak Movement: %.2f
            Average Movement Change: %.2f
            
            Smoothness: $smoothnessRating
            Performance Rating: $performanceRating
            
            Lower average movement change means the movement was smoother and more controlled.
        """.trimIndent().format(
            elapsedSeconds,
            averageMovement,
            peakMovement,
            averageChange
        )

        Toast.makeText(this, "Movement test stopped.", Toast.LENGTH_SHORT).show()
    }

    private fun resetTestData() {
        isTesting = false
        peakMovement = 0.0
        lastMovementStrength = null
        startTime = 0L
        movementSamples.clear()
        movementChanges.clear()

        tvCurrentMovement.text = "Current Movement: 0.00"
        tvPeakMovement.text = "Peak Movement: 0.00"
        tvSmoothness.text = "Smoothness: Not measured"
        tvElapsedMovementTime.text = "Elapsed Time: 0.0 seconds"
    }

    private fun getSmoothnessRating(averageChange: Double): String {
        return when {
            averageChange < 0.20 -> "Very smooth"
            averageChange < 0.50 -> "Smooth"
            averageChange < 1.00 -> "Moderate control"
            else -> "Jerky movement"
        }
    }

    private fun getPerformanceRating(peak: Double, averageChange: Double): String {
        return when {
            peak < 11.0 && averageChange < 0.30 -> "Excellent control"
            peak < 13.0 && averageChange < 0.60 -> "Good control"
            peak < 15.0 -> "Moderate control"
            else -> "Needs improvement"
        }
    }
}