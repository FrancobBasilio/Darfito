package com.tuusuario.darfito.ui

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
import com.tuusuario.darfito.HomeActivity
import com.tuusuario.darfito.R
import com.tuusuario.darfito.adapter.RankingAdapter
import com.tuusuario.darfito.data.dao.PlayerDAO
import com.tuusuario.darfito.data.dao.UsuarioDAO
import com.tuusuario.darfito.model.Player

class HomeFragment : Fragment() {

    private lateinit var rvRanking: RecyclerView
    private lateinit var btnPlay: MaterialButton
    private lateinit var rankingAdapter: RankingAdapter

    // DAOs
    private lateinit var playerDAO: PlayerDAO
    private lateinit var usuarioDAO: UsuarioDAO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar DAOs
        playerDAO = PlayerDAO(requireContext())
        usuarioDAO = UsuarioDAO(requireContext())

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
        // Obtener TODOS los players ordenados por score desde la BD
        val allPlayers = playerDAO.obtenerTodos()

        // Mostrar TODOS en el RecyclerView (desde el puesto #1)
        rankingAdapter.updatePlayers(allPlayers)
    }

    private fun startGame() {
        val usuarioId = (activity as? HomeActivity)?.obtenerUsuarioId() ?: -1

        if (usuarioId == -1) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(requireContext(), GameModeActivity::class.java)
        intent.putExtra("usuario_id", usuarioId)
        startActivity(intent)
    }

    private fun showPlayerProfile(player: Player) {
        val usuarioId = (activity as? HomeActivity)?.obtenerUsuarioId() ?: -1
        val dialog = PlayerProfileDialogFragment.newInstance(player, usuarioId)
        dialog.show(parentFragmentManager, "PlayerProfileDialog")
    }

    override fun onResume() {
        super.onResume()
        // Recargar ranking cuando se vuelve al fragment
        loadRankingData()
    }
}