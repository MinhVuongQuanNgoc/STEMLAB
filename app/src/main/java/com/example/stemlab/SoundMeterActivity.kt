package com.example.stemlab

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.math.log10

class SoundMeterActivity : AppCompatActivity() {

    private var mediaRecorder: MediaRecorder? = null
    private lateinit var handler: Handler
    private var isMeasuring = false

    private lateinit var tvSoundLevel: TextView
    private lateinit var tvSoundCategory: TextView

    companion object {
        private const val RECORD_AUDIO_REQUEST_CODE = 2001
    }

    private val soundRunnable = object : Runnable {
        override fun run() {
            if (isMeasuring) {
                updateSoundLevel()
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_meter)

        handler = Handler(Looper.getMainLooper())

        tvSoundLevel = findViewById(R.id.tvSoundLevel)
        tvSoundCategory = findViewById(R.id.tvSoundCategory)

        val btnStartSoundMeter = findViewById<Button>(R.id.btnStartSoundMeter)
        val btnStopSoundMeter = findViewById<Button>(R.id.btnStopSoundMeter)
        val btnBackFromSoundMeter = findViewById<Button>(R.id.btnBackFromSoundMeter)

        btnStartSoundMeter.setOnClickListener {
            if (hasAudioPermission()) {
                startMeasuring()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_AUDIO_REQUEST_CODE
                )
            }
        }

        btnStopSoundMeter.setOnClickListener {
            stopMeasuring()
        }

        btnBackFromSoundMeter.setOnClickListener {
            finish()
        }
    }

    private fun hasAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startMeasuring() {
        if (isMeasuring) {
            return
        }

        try {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            val outputFile = "${cacheDir.absolutePath}/sound_meter.3gp"

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile)
                prepare()
                start()
            }

            isMeasuring = true
            Toast.makeText(this, "Sound measuring started.", Toast.LENGTH_SHORT).show()
            handler.post(soundRunnable)

        } catch (exception: Exception) {
            Toast.makeText(
                this,
                "Could not start sound meter: ${exception.message}",
                Toast.LENGTH_LONG
            ).show()
            stopMeasuring()
        }
    }

    private fun updateSoundLevel() {
        val amplitude = mediaRecorder?.maxAmplitude ?: 0

        if (amplitude > 0) {
            val estimatedDb = (20 * log10(amplitude.toDouble())).toInt()
            val category = getSoundCategory(estimatedDb)

            tvSoundLevel.text = "Estimated Sound Level: $estimatedDb dB"
            tvSoundCategory.text = "Category: $category"
        } else {
            tvSoundLevel.text = "Estimated Sound Level: 0 dB"
            tvSoundCategory.text = "Category: Waiting for sound..."
        }
    }

    private fun getSoundCategory(db: Int): String {
        return when {
            db < 40 -> "Quiet"
            db < 60 -> "Normal classroom level"
            db < 80 -> "Loud"
            else -> "Very loud"
        }
    }

    private fun stopMeasuring() {
        isMeasuring = false
        handler.removeCallbacks(soundRunnable)

        try {
            mediaRecorder?.stop()
        } catch (_: Exception) {
            // Ignore stop errors if recorder was not fully started.
        }

        mediaRecorder?.release()
        mediaRecorder = null

        Toast.makeText(this, "Sound measuring stopped.", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (
            requestCode == RECORD_AUDIO_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startMeasuring()
        } else {
            Toast.makeText(this, "Microphone permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopMeasuring()
    }
}