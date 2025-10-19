package com.tuusuario.darfito.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuusuario.darfito.R
import com.tuusuario.darfito.data.dao.UsuarioDAO
import com.tuusuario.darfito.model.Player
import java.text.NumberFormat
import java.util.Locale

class RankingAdapter(
    private val onPlayerClick: (Player) -> Unit) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    private var players: List<Player> = emptyList()
    private var usuarioDAO: UsuarioDAO? = null

    fun updatePlayers(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)

        // Inicializar DAO
        if (usuarioDAO == null) {
            usuarioDAO = UsuarioDAO(parent.context)
        }

        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        // La posición comienza desde 1 (primer lugar)
        holder.bind(players[position], position + 1)
    }

    override fun getItemCount() = players.size

    inner class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)
        private val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvPlayerName: TextView = itemView.findViewById(R.id.tvPlayerName)
        private val tvPlayerLevel: TextView = itemView.findViewById(R.id.tvPlayerLevel)
        private val tvPlayerScore: TextView = itemView.findViewById(R.id.tvPlayerScore)

        fun bind(player: Player, position: Int) {
            // Mostrar posición para el top 3
            tvPosition.text = when (position) {
                1 -> "🥇"
                2 -> "🥈"
                3 -> "🥉"
                else -> "$position°"
            }

            // Obtener nombre del usuario desde la BD
            val usuario = usuarioDAO?.obtenerPorId(player.usuarioId)
            tvPlayerName.text = usuario?.nombres ?: "Jugador ${player.usuarioId}"

            tvPlayerLevel.text = player.level

            // Formatear score
            val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault())
                .format(player.score)
            tvPlayerScore.text = formattedScore

            // Cargar avatar
            ivAvatar.setImageResource(player.avatarResId)

            // Click listener
            itemView.setOnClickListener {
                onPlayerClick(player)
            }
        }
    }
}