package com.tuusuario.darfito.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuusuario.darfito.GameModeActivity
import com.tuusuario.darfito.R
import com.tuusuario.darfito.adapter.RankingAdapter
import com.tuusuario.darfito.model.Player

class HomeFragment : Fragment() {

    private lateinit var rvRanking: RecyclerView
    private lateinit var btnPlay: MaterialButton
    private lateinit var rankingAdapter: RankingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRanking()
        setupClickListeners()
        loadRankingData()
    }

    private fun initViews(view: View) {
        rvRanking = view.findViewById(R.id.rvRanking)
        btnPlay = view.findViewById(R.id.btnPlay)
    }

    private fun setupRanking() {
        rankingAdapter = RankingAdapter { player ->
            showPlayerProfile(player)
        }

        rvRanking.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rankingAdapter
        }
    }

    private fun setupClickListeners() {
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
    }

    private fun startGame() {
        val intent = Intent(requireContext(), GameModeActivity::class.java)
        startActivity(intent)
    }

    private fun showPlayerProfile(player: Player) {
        Toast.makeText(requireContext(), "Ver perfil de ${player.name}", Toast.LENGTH_SHORT).show()
    }
}