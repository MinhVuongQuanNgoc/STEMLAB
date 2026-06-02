package com.example.stemlab

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import utils.UIHelper
import java.io.File
import kotlin.math.PI

class HandFanActivity : AppCompatActivity() {

    private var videoUri: Uri? = null
    private var videoFile: File? = null

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val VIDEO_CAPTURE_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_fan)

        val etFanDesign = findViewById<EditText>(R.id.etFanDesign)
        val etFanDistance = findViewById<EditText>(R.id.etFanDistance)
        val etBendAngle = findViewById<EditText>(R.id.etBendAngle)
        val rgMaterial = findViewById<RadioGroup>(R.id.rgMaterial)
        val btnCalculateFanForce = findViewById<Button>(R.id.btnCalculateFanForce)
        val btnBackFromHandFan = findViewById<ImageButton>(R.id.btnBackFromHandFan)
        val tvFanResult = findViewById<TextView>(R.id.tvFanResult)

        val btnRecordVideo = findViewById<Button>(R.id.btnRecordVideo)
        val btnViewVideo = findViewById<Button>(R.id.btnViewVideo)

        rgMaterial.check(R.id.rbThinPaper)

        btnRecordVideo.setOnClickListener {
            if (checkCameraPermission()) {
                dispatchTakeVideoIntent()
            } else {
                requestCameraPermission()
            }
        }

        btnViewVideo.setOnClickListener {
            videoUri?.let { uri ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "video/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
        }

        btnCalculateFanForce.setOnClickListener {
            val design = etFanDesign.text.toString().trim()
            val distanceText = etFanDistance.text.toString().trim()
            val angleText = etBendAngle.text.toString().trim()

            if (distanceText.isEmpty() || angleText.isEmpty()) {
                UIHelper.showNotification(it, "Please enter distance and bend angle.")
                return@setOnClickListener
            }

            val distance = distanceText.toDoubleOrNull()
            val angleDegrees = angleText.toDoubleOrNull()

            if (distance == null || angleDegrees == null) {
                UIHelper.showNotification(it, "Please enter valid numbers.")
                return@setOnClickListener
            }

            val materialInfo = getMaterialInfo(rgMaterial.checkedRadioButtonId)
            val materialName = materialInfo.first
            val stiffnessK = materialInfo.second

            val angleRadians = angleDegrees * PI / 180.0
            val estimatedForce = stiffnessK * angleRadians

            val forceCategory = when {
                estimatedForce < 0.05 -> "Low air force"
                estimatedForce < 0.20 -> "Moderate air force"
                else -> "High air force"
            }

            tvFanResult.text = """
                Hand Fan Result
                
                Design / Notes: ${if (design.isEmpty()) "Not recorded" else design}
                Material: $materialName
                Distance: %.1f cm
                Bend Angle: %.1f°
                Angle in Radians: %.3f rad
                
                Formula: F ≈ k × θ
                k value: %.2f N/rad
                Estimated Force: %.3f N
                
                Interpretation: $forceCategory
            """.trimIndent().format(
                distance,
                angleDegrees,
                angleRadians,
                stiffnessK,
                estimatedForce
            )
        }

        btnBackFromHandFan.setOnClickListener {
            finish()
        }
    }

    private fun getMaterialInfo(selectedId: Int): Pair<String, Double> {
        return when (selectedId) {
            R.id.rbCardStock -> Pair("Standard card stock", 0.2)
            R.id.rbThinCardboard -> Pair("Thin cardboard", 0.5)
            R.id.rbCorrugatedCardboard -> Pair("Corrugated cardboard", 2.5)
            else -> Pair("Thin printer paper", 0.05)
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun dispatchTakeVideoIntent() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        
        // Create a temporary file in the cache directory
        videoFile = File(cacheDir, "temp_fan_video.mp4")
        videoUri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            videoFile!!
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, VIDEO_CAPTURE_CODE)
        } else {
            UIHelper.showNotification(findViewById(android.R.id.content), "No camera app found.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_CAPTURE_CODE && resultCode == RESULT_OK) {
            findViewById<Button>(R.id.btnViewVideo).isEnabled = true
            UIHelper.showNotification(findViewById(android.R.id.content), "Video captured successfully.")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakeVideoIntent()
            } else {
                UIHelper.showNotification(findViewById(android.R.id.content), "Camera permission is required to record experiments.")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Delete the temporary video file when exiting the activity
        videoFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }
    }
}