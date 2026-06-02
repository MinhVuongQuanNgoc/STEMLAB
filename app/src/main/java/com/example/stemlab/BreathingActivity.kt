package com.example.stemlab

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs
import kotlin.math.sqrt

class BreathingActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var etBreathingNotes: EditText
    private lateinit var tvChestMovement: TextView
    private lateinit var tvDetectedBreaths: TextView
    private lateinit var tvBreathingElapsedTime: TextView
    private lateinit var tvBreathsPerMinute: TextView
    private lateinit var tvBreathingCategory: TextView
    private lateinit var tvBreathingResult: TextView

    private var isTesting = false
    private var startTime = 0L
    private var breathCount = 0
    private var baselineMovement: Double? = null
    private var lastBreathTime = 0L
    private var isAboveThreshold = false

    companion object {
        private const val BREATH_THRESHOLD = 0.35
        private const val MIN_BREATH_INTERVAL_MS = 1200L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breathing)

        etBreathingNotes = findViewById(R.id.etBreathingNotes)
        tvChestMovement = findViewById(R.id.tvChestMovement)
        tvDetectedBreaths = findViewById(R.id.tvDetectedBreaths)
        tvBreathingElapsedTime = findViewById(R.id.tvBreathingElapsedTime)
        tvBreathsPerMinute = findViewById(R.id.tvBreathsPerMinute)
        tvBreathingCategory = findViewById(R.id.tvBreathingCategory)
        tvBreathingResult = findViewById(R.id.tvBreathingResult)

        val btnStartBreathingTest = findViewById<Button>(R.id.btnStartBreathingTest)
        val btnStopBreathingTest = findViewById<Button>(R.id.btnStopBreathingTest)
        val btnResetBreathingTest = findViewById<Button>(R.id.btnResetBreathingTest)
        val btnBackFromBreathing = findViewById<android.widget.ImageButton>(R.id.btnBackFromBreathing)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            UIHelper.showNotification(btnStartBreathingTest, "Accelerometer sensor not available.")
        }

        btnStartBreathingTest.setOnClickListener {
            resetBreathingData()
            isTesting = true
            startTime = System.currentTimeMillis()
            tvBreathingResult.text = "Breathing test started. Breathe normally and keep the phone steady."
            UIHelper.showNotification(it, "Breathing test started.")
        }

        btnStopBreathingTest.setOnClickListener {
            stopAndShowResult()
        }

        btnResetBreathingTest.setOnClickListener {
            resetBreathingData()
            tvBreathingResult.text = "Result reset."
        }

        btnBackFromBreathing.setOnClickListener {
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

            if (baselineMovement == null) {
                baselineMovement = movementStrength
            }

            val movementChange = abs(movementStrength - (baselineMovement ?: movementStrength))
            detectBreath(movementChange)

            val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0
            val breathsPerMinute = if (elapsedSeconds > 0) {
                breathCount / elapsedSeconds * 60.0
            } else {
                0.0
            }

            tvChestMovement.text = "Chest Movement: %.2f".format(movementChange)
            tvDetectedBreaths.text = "Detected Breaths: $breathCount"
            tvBreathingElapsedTime.text = "Elapsed Time: %.1f seconds".format(elapsedSeconds)
            tvBreathsPerMinute.text = "Breaths Per Minute: %.1f".format(breathsPerMinute)
            tvBreathingCategory.text = "Category: ${getBreathingCategory(breathsPerMinute)}"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed for this prototype.
    }

    private fun detectBreath(movementChange: Double) {
        val currentTime = System.currentTimeMillis()

        if (
            movementChange > BREATH_THRESHOLD &&
            !isAboveThreshold &&
            currentTime - lastBreathTime > MIN_BREATH_INTERVAL_MS
        ) {
            breathCount++
            lastBreathTime = currentTime
            isAboveThreshold = true
        }

        if (movementChange < BREATH_THRESHOLD / 2) {
            isAboveThreshold = false
        }
    }

    private fun stopAndShowResult() {
        if (!isTesting) {
            UIHelper.showNotification(findViewById(R.id.btnStopBreathingTest), "No active breathing test.")
            return
        }

        isTesting = false

        val notes = etBreathingNotes.text.toString().trim()
        val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000.0
        val breathsPerMinute = if (elapsedSeconds > 0) {
            breathCount / elapsedSeconds * 60.0
        } else {
            0.0
        }

        tvBreathingResult.text = """
            Breathing Test Result
            
            Notes: ${if (notes.isEmpty()) "Not recorded" else notes}
            Elapsed Time: %.1f seconds
            Detected Breaths: $breathCount
            Breaths Per Minute: %.1f
            
            Category: ${getBreathingCategory(breathsPerMinute)}
            
            This is an estimated reading based on phone movement, suitable for classroom comparison.
        """.trimIndent().format(
            elapsedSeconds,
            breathsPerMinute
        )

        UIHelper.showNotification(findViewById(R.id.btnStopBreathingTest), "Breathing test stopped.")
    }

    private fun resetBreathingData() {
        isTesting = false
        startTime = 0L
        breathCount = 0
        baselineMovement = null
        lastBreathTime = 0L
        isAboveThreshold = false

        tvChestMovement.text = "Chest Movement: 0.00"
        tvDetectedBreaths.text = "Detected Breaths: 0"
        tvBreathingElapsedTime.text = "Elapsed Time: 0.0 seconds"
        tvBreathsPerMinute.text = "Breaths Per Minute: 0.0"
        tvBreathingCategory.text = "Category: Not measured"
    }

    private fun getBreathingCategory(bpm: Double): String {
        return when {
            bpm == 0.0 -> "Not enough data"
            bpm < 10 -> "Slow breathing"
            bpm <= 20 -> "Normal resting range"
            bpm <= 30 -> "Elevated breathing"
            else -> "Fast breathing after activity"
        }
    }
}
