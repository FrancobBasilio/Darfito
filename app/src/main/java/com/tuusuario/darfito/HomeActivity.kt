package com.tuusuario.darfito

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuusuario.darfito.adapter.RankingAdapter
import com.tuusuario.darfito.model.Player

class HomeActivity : AppCompatActivity() {

    private lateinit var rvRanking: RecyclerView
    private lateinit var btnPlay: MaterialButton
    private lateinit var rankingAdapter: RankingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupRanking()
        setupClickListeners()
        loadRankingData()
    }

    private fun initViews() {
        rvRanking = findViewById(R.id.rvRanking)
        btnPlay = findViewById(R.id.btnPlay)
    }

    private fun setupRanking() {
        rankingAdapter = RankingAdapter { player ->

            showPlayerProfile(player)
        }

        rvRanking.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = rankingAdapter
        }
    }

    private fun setupClickListeners() {
        btnPlay.setOnClickListener {
            startGame()
        }

        findViewById<android.widget.ImageButton>(R.id.btnLogout).setOnClickListener {
            logout()
        }
    }

    private fun loadRankingData() {

        val players = listOf(
            Player("4", "Luis Martín", 2180, "Nivel Experto"),
            Player("5", "Sofia López", 1950, "Nivel Avanzado"),
            Player("6", "Diego Sánchez", 1820, "Nivel Avanzado"),
            Player("7", "Carmen Ruiz", 1675, "Nivel Intermedio"),
            Player("8", "Pedro Jiménez", 1540, "Nivel Intermedio"),
            Player("9", "Laura García", 1420, "Nivel Intermedio"),
            Player("10", "Miguel Torres", 1350, "Nivel Básico"),
            Player("11", "Elena Morales", 1280, "Nivel Básico"),
            Player("12", "Javier Herrera", 1150, "Nivel Básico"),
            Player("13", "Isabel Vargas", 1080, "Nivel Básico")
        )

        rankingAdapter.updatePlayers(players)
        updateTopThree()
    }

    private fun updateTopThree() {

    }

    private fun startGame() {

        val intent = Intent(this, GameModeActivity::class.java)
        startActivity(intent)
    }

    private fun showPlayerProfile(player: Player) {
        Toast.makeText(this, "Ver perfil de ${player.name}", Toast.LENGTH_SHORT).show()

    }

    private fun logout() {

        val intent = Intent(this, AccesoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}