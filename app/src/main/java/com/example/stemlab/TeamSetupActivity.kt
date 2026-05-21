package com.example.stemlab

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class TeamSetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_setup)

        val etTeamName = findViewById<EditText>(R.id.etTeamName)
        val etMemberOne = findViewById<EditText>(R.id.etMemberOne)
        val etMemberTwo = findViewById<EditText>(R.id.etMemberTwo)
        val etGradeLevel = findViewById<EditText>(R.id.etGradeLevel)
        val btnCreateTeam = findViewById<Button>(R.id.btnCreateTeam)
        val tvTeamCode = findViewById<TextView>(R.id.tvTeamCode)

        btnCreateTeam.setOnClickListener {
            val teamName = etTeamName.text.toString().trim()
            val memberOne = etMemberOne.text.toString().trim()
            val memberTwo = etMemberTwo.text.toString().trim()
            val gradeLevel = etGradeLevel.text.toString().trim()

            if (teamName.isEmpty() || memberOne.isEmpty() || gradeLevel.isEmpty()) {
                Toast.makeText(this, "Please enter team name, member name, and grade level.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val teamCode = "TEAM-${Random.nextInt(1000, 9999)}"

            tvTeamCode.text = """
                
                Team Created Successfully!
                
                Team: $teamName
                Members: $memberOne, $memberTwo
                Grade: $gradeLevel
                Team Code: $teamCode
            """.trimIndent()

            val teamPreferences = getSharedPreferences("STEMM_TEAM", MODE_PRIVATE)
            teamPreferences.edit()
                .putString("teamName", teamName)
                .apply()

            val intent = Intent(this, ActivityListActivity::class.java)
            startActivity(intent)
        }
    }
}