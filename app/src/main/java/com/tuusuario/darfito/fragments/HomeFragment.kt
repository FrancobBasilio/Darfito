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
import com.tuusuario.darfito.PlayerProfileDialogFragment
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
            Player("CP04", "Luis Martín", 2180, "Nivel Experto", R.drawable.ic_person),
            Player("CP05", "Sofia López", 1950, "Nivel Avanzado", R.drawable.ic_person),
            Player("CP06", "Diego Sánchez", 1820, "Nivel Avanzado", R.drawable.ic_person),
            Player("CP07", "Carmen Ruiz", 1675, "Nivel Intermedio", R.drawable.ic_person),
            Player("CP08", "Pedro Jiménez", 1540, "Nivel Intermedio", R.drawable.ic_person),
            Player("CP09", "Laura García", 1420, "Nivel Intermedio", R.drawable.ic_person),
            Player("CP010", "Miguel Torres", 1350, "Nivel Básico", R.drawable.ic_person),
            Player("CP011", "Elena Morales", 1280, "Nivel Básico", R.drawable.ic_person),
            Player("CP012", "Javier Herrera", 1150, "Nivel Básico", R.drawable.ic_person),
            Player("CP013", "Isabel Vargas", 1080, "Nivel Básico", R.drawable.ic_person)
        )

        rankingAdapter.updatePlayers(players)
    }

    private fun startGame() {
        val intent = Intent(requireContext(), GameModeActivity::class.java)
        startActivity(intent)
    }

    private fun showPlayerProfile(player: Player) {
        val dialog = PlayerProfileDialogFragment.newInstance(player)
        dialog.show(parentFragmentManager, "PlayerProfileDialog")
    }
}