package com.tuusuario.darfito.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuusuario.darfito.HomeActivity
import com.tuusuario.darfito.R
import com.tuusuario.darfito.adapter.AmigoAdapter
import com.tuusuario.darfito.data.dao.AmigoDAO
import com.tuusuario.darfito.data.dao.PlayerDAO
import com.tuusuario.darfito.data.dao.UsuarioDAO
import com.tuusuario.darfito.model.Player
import com.tuusuario.darfito.model.Usuario

class AmigosFragment : Fragment() {

    private lateinit var rvAmigos: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var amigoAdapter: AmigoAdapter

    // DAOs
    private lateinit var amigoDAO: AmigoDAO
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var playerDAO: PlayerDAO

    private var usuarioId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_amigos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar DAOs
        amigoDAO = AmigoDAO(requireContext())
        usuarioDAO = UsuarioDAO(requireContext())
        playerDAO = PlayerDAO(requireContext())

        // Obtener usuario ID
        usuarioId = (activity as? HomeActivity)?.obtenerUsuarioId() ?: -1

        initViews(view)
        setupRecyclerView()
        cargarAmigos()
    }

    private fun initViews(view: View) {
        rvAmigos = view.findViewById(R.id.rvAmigos)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
    }

    private fun setupRecyclerView() {
        amigoAdapter = AmigoAdapter(
            onJugar = { usuario, player ->
                iniciarPartidaMultijugador(usuario, player)
            },
            onEliminar = { usuario ->
                mostrarDialogoEliminar(usuario)
            }
        )

        rvAmigos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = amigoAdapter
        }
    }

    private fun cargarAmigos() {
        if (usuarioId == -1) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener IDs de amigos
        val amigosIds = amigoDAO.obtenerAmigos(usuarioId)

        // Cargar información completa de cada amigo
        val amigosCompletos = amigosIds.mapNotNull { amigoId ->
            val usuario = usuarioDAO.obtenerPorId(amigoId)
            val player = usuario?.let { playerDAO.buscarPorUsuarioId(it.id) }
            usuario?.let { Pair(it, player) }
        }

        // Actualizar adapter
        amigoAdapter.updateAmigos(amigosCompletos)

        // Mostrar mensaje si no hay amigos
        if (amigosCompletos.isEmpty()) {
            rvAmigos.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = "Aún no tienes amigos.\n¡Acepta solicitudes o busca jugadores en el ranking!"
        } else {
            rvAmigos.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
        }
    }

    private fun iniciarPartidaMultijugador(usuario: Usuario, player: Player?) {
        // TODO: Implementar sistema de partidas multijugador
        // Por ahora mostrar mensaje informativo
        AlertDialog.Builder(requireContext())
            .setTitle("🎮 Partida Multijugador")
            .setMessage("¿Deseas desafiar a ${usuario.nombres} a una partida?\n\n" +
                    "Score actual: ${player?.score ?: 0}\n" +
                    "Nivel: ${player?.level ?: "Básico"}")
            .setPositiveButton("Desafiar") { dialog, _ ->
                Toast.makeText(
                    requireContext(),
                    "Funcionalidad de multijugador en desarrollo",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()

                // AQUÍ puedes redirigir a una nueva Activity de multijugador:
                // val intent = Intent(requireContext(), MultiplayerGameActivity::class.java)
                // intent.putExtra("OPONENTE_ID", usuario.id)
                // startActivity(intent)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarDialogoEliminar(usuario: Usuario) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Amigo")
            .setMessage("¿Estás seguro de eliminar a ${usuario.nombres} de tus amigos?")
            .setPositiveButton("Sí") { dialog, _ ->
                eliminarAmigo(usuario)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun eliminarAmigo(usuario: Usuario) {
        val eliminado = amigoDAO.eliminar(usuarioId, usuario.id)

        if (eliminado) {
            Toast.makeText(
                requireContext(),
                "${usuario.nombres} ha sido eliminado de tus amigos",
                Toast.LENGTH_SHORT
            ).show()

            // Recargar lista
            cargarAmigos()
        } else {
            Toast.makeText(
                requireContext(),
                "Error al eliminar amigo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarAmigos()
    }
}

