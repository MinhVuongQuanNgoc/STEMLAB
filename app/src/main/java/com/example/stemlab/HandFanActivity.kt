package com.example.stemlab

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.PI

class HandFanActivity : AppCompatActivity() {

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

        rgMaterial.check(R.id.rbThinPaper)

        btnCalculateFanForce.setOnClickListener {
            val design = etFanDesign.text.toString().trim()
            val distanceText = etFanDistance.text.toString().trim()
            val angleText = etBendAngle.text.toString().trim()

            if (distanceText.isEmpty() || angleText.isEmpty()) {
                ToastHelper.showCustomToast(this, "Please enter distance and bend angle.")
                return@setOnClickListener
            }

            val distance = distanceText.toDoubleOrNull()
            val angleDegrees = angleText.toDoubleOrNull()

            if (distance == null || angleDegrees == null) {
                ToastHelper.showCustomToast(this, "Please enter valid numbers.")
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
}