package com.example.stemlab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var rvLeaderboard: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvEmptyState: TextView
    private val leaderboardEntries = mutableListOf<LeaderboardEntry>()
    private lateinit var adapter: LeaderboardAdapter

    data class LeaderboardEntry(
        val rank: Int,
        val teamName: String,
        val challengeTitle: String,
        val score: Long,
        val rating: Long
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val btnBackFromLeaderboard = findViewById<android.widget.ImageButton>(R.id.btnBackFromLeaderboard)
        btnBackFromLeaderboard.setOnClickListener {
            finish()
        }

        rvLeaderboard = findViewById(R.id.rvLeaderboard)
        pbLoading = findViewById(R.id.pbLoading)
        tvEmptyState = findViewById(R.id.tvEmptyState)

        rvLeaderboard.layoutManager = LinearLayoutManager(this)
        adapter = LeaderboardAdapter(leaderboardEntries)
        rvLeaderboard.adapter = adapter

        fetchLeaderboardData()
    }

    private fun fetchLeaderboardData() {
        val firestore = FirebaseFirestore.getInstance()
        
        pbLoading.visibility = View.VISIBLE
        tvEmptyState.visibility = View.GONE

        firestore.collection("results")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { documents ->
                pbLoading.visibility = View.GONE
                leaderboardEntries.clear()
                
                if (documents.isEmpty) {
                    tvEmptyState.visibility = View.VISIBLE
                } else {
                    var rank = 1
                    for (document in documents) {
                        val teamName = document.getString("teamName") ?: "Unknown Team"
                        val challengeTitle = document.getString("challengeTitle") ?: "Unknown Challenge"
                        val score = document.getLong("score") ?: 0
                        val rating = document.getLong("rating") ?: 0
                        
                        leaderboardEntries.add(LeaderboardEntry(rank, teamName, challengeTitle, score, rating))
                        rank++
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                pbLoading.visibility = View.GONE
                tvEmptyState.text = "Failed to load leaderboard."
                tvEmptyState.visibility = View.VISIBLE
                UIHelper.showNotification(
                    rvLeaderboard,
                    "Leaderboard error: ${exception.message}"
                )
            }
    }

    class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) :
        RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvRank: TextView = view.findViewById(R.id.tvRank)
            val tvTeamName: TextView = view.findViewById(R.id.tvTeamName)
            val tvChallenge: TextView = view.findViewById(R.id.tvChallenge)
            val tvRating: TextView = view.findViewById(R.id.tvRating)
            val tvScore: TextView = view.findViewById(R.id.tvScore)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_leaderboard, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val entry = entries[position]
            holder.tvRank.text = "#${entry.rank}"
            holder.tvTeamName.text = entry.teamName
            holder.tvChallenge.text = entry.challengeTitle
            holder.tvRating.text = "Rating: ${entry.rating}/5"
            holder.tvScore.text = entry.score.toString()
            
            // Highlight top 3
            when (entry.rank) {
                1 -> holder.tvRank.setBackgroundColor(android.graphics.Color.parseColor("#FFD700")) // Gold
                2 -> holder.tvRank.setBackgroundColor(android.graphics.Color.parseColor("#C0C0C0")) // Silver
                3 -> holder.tvRank.setBackgroundColor(android.graphics.Color.parseColor("#CD7F32")) // Bronze
                else -> holder.tvRank.setBackgroundColor(android.graphics.Color.parseColor("#E8F5E9"))
            }
        }

        override fun getItemCount() = entries.size
    }
}