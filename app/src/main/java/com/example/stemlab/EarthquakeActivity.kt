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
import kotlin.math.sqrt

class EarthquakeActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var etStructureDesign: EditText
    private lateinit var tvCurrentMovement: TextView
    private lateinit var tvPeakMovement: TextView
    private lateinit var tvStabilityRating: TextView
    private lateinit var tvEarthquakeResult: TextView

    private var isTesting = false
    private var peakMovement = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earthquake)

        etStructureDesign = findViewById(R.id.etStructureDesign)
        tvCurrentMovement = findViewById(R.id.tvCurrentMovement)
        tvPeakMovement = findViewById(R.id.tvPeakMovement)
        tvStabilityRating = findViewById(R.id.tvStabilityRating)
        tvEarthquakeResult = findViewById(R.id.tvEarthquakeResult)

        val btnStartEarthquakeTest = findViewById<Button>(R.id.btnStartEarthquakeTest)
        val btnStopEarthquakeTest = findViewById<Button>(R.id.btnStopEarthquakeTest)
        val btnResetEarthquakeTest = findViewById<Button>(R.id.btnResetEarthquakeTest)
        val btnBackFromEarthquake = findViewById<Button>(R.id.btnBackFromEarthquake)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            Toast.makeText(this, "Accelerometer sensor not available.", Toast.LENGTH_LONG).show()
        }

        btnStartEarthquakeTest.setOnClickListener {
            peakMovement = 0.0
            isTesting = true
            tvEarthquakeResult.text = "Testing started. Gently shake the structure."
            Toast.makeText(this, "Vibration test started.", Toast.LENGTH_SHORT).show()
        }

        btnStopEarthquakeTest.setOnClickListener {
            isTesting = false

            val design = etStructureDesign.text.toString().trim()
            val rating = getStabilityRating(peakMovement)

            tvEarthquakeResult.text = """
                Earthquake Test Result
                
                Design: ${if (design.isEmpty()) "Not recorded" else design}
                Peak Movement: %.2f
                Stability Rating: $rating
                
                Lower peak movement means the structure reduced vibration more effectively.
            """.trimIndent().format(peakMovement)

            Toast.makeText(this, "Vibration test stopped.", Toast.LENGTH_SHORT).show()
        }

        btnResetEarthquakeTest.setOnClickListener {
            peakMovement = 0.0
            tvPeakMovement.text = "Peak Movement: 0.00"
            tvStabilityRating.text = "Stability Rating: Not measured"
            tvEarthquakeResult.text = "Result reset."
        }

        btnBackFromEarthquake.setOnClickListener {
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

            if (movementStrength > peakMovement) {
                peakMovement = movementStrength
            }

            val rating = getStabilityRating(peakMovement)

            tvCurrentMovement.text = "Current Movement: %.2f".format(movementStrength)
            tvPeakMovement.text = "Peak Movement: %.2f".format(peakMovement)
            tvStabilityRating.text = "Stability Rating: $rating"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed for this prototype.
    }

    private fun getStabilityRating(peak: Double): String {
        return when {
            peak < 10.5 -> "Excellent stability"
            peak < 12.0 -> "Good stability"
            peak < 15.0 -> "Moderate stability"
            else -> "Poor stability"
        }
    }
}