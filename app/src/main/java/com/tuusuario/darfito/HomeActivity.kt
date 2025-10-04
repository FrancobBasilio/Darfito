package com.tuusuario.darfito

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.tuusuario.darfito.adapter.RankingAdapter
import com.tuusuario.darfito.model.Player

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nv_side: NavigationView
    private lateinit var mainLayout: LinearLayout
    private lateinit var btnMenu: ImageButton

    private lateinit var tvBienvenida: TextView
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
        stupBienvenida()
        setupNavigationView()
        setupBackPressed()
    }

    private fun initViews() {
        rvRanking = findViewById(R.id.rvRanking)
        btnPlay = findViewById(R.id.btnPlay)
        tvBienvenida = findViewById(R.id.tvBienvenida)
        drawerLayout = findViewById(R.id.drawerLayout)
        nv_side = findViewById(R.id.nv_side)
        mainLayout = findViewById(R.id.mainLayout)
        btnMenu = findViewById(R.id.btnMenu)

    }

    private fun setupNavigationView() {
        nv_side.setNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.snv_perfil -> {

                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                }
                R.id.snv_amigos -> {

                    Toast.makeText(this, "Amigos", Toast.LENGTH_SHORT).show()
                }
                R.id.snv_solicitudes -> {

                    Toast.makeText(this, "Solicitudes", Toast.LENGTH_SHORT).show()
                }
                R.id.snv_log_out -> {
                    cambioActivity(LoginActivity::class.java)
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    finish()
                }
            }
        })
    }



    private fun setupClickListeners() {
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
        btnPlay.setOnClickListener {
            startGame()
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

    private fun stupBienvenida() {
        val nombreUsuario = intent.getStringExtra("usuario_nombre")
        tvBienvenida.text = "Bienvenido a Trivia, $nombreUsuario"
    }

    private fun showPlayerProfile(player: Player) {
        Toast.makeText(this, "Ver perfil de ${player.name}", Toast.LENGTH_SHORT).show()
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

    private fun cambioActivity(activityDestino: Class<out Activity>) {
        val intent = Intent(this, activityDestino)
        startActivity(intent)
    }
}