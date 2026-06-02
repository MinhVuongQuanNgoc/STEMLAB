package com.example.stemlab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageButton>(R.id.btnBackFromSettings)
        val tvTeamName = findViewById<TextView>(R.id.tvSettingsTeamName)
        val tvMemberOne = findViewById<TextView>(R.id.tvSettingsMemberOne)
        val tvMemberTwo = findViewById<TextView>(R.id.tvSettingsMemberTwo)
        val tvGrade = findViewById<TextView>(R.id.tvSettingsGrade)
        val switchDarkMode = findViewById<MaterialSwitch>(R.id.switchDarkMode)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Load Team Info
        val prefs = getSharedPreferences("STEMM_TEAM", MODE_PRIVATE)
        tvTeamName.text = "Team: ${prefs.getString("teamName", "N/A")}"
        tvMemberOne.text = "Member 1: ${prefs.getString("memberOne", "N/A")}"
        tvMemberTwo.text = "Member 2: ${prefs.getString("memberTwo", "N/A")}"
        tvGrade.text = "Grade Level: ${prefs.getString("gradeLevel", "N/A")}"

        // Dark Mode Toggle
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        switchDarkMode.isChecked = isDarkMode

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
