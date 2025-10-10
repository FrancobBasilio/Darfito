package com.tuusuario.darfito.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tuusuario.darfito.R
import com.tuusuario.darfito.model.Player
import java.text.NumberFormat
import java.util.*

class RankingAdapter(
    private val onPlayerClick: (Player) -> Unit
) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    private var players: List<Player> = emptyList()

    fun updatePlayers(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.bind(players[position], position + 4)
    }

    override fun getItemCount() = players.size

    inner class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPosition: TextView = itemView.findViewById(R.id.tvPosition)
        private val ivAvatar: ImageView = itemView.findViewById(R.id.ivAvatar)
        private val tvPlayerName: TextView = itemView.findViewById(R.id.tvPlayerName)
        private val tvPlayerLevel: TextView = itemView.findViewById(R.id.tvPlayerLevel)
        private val tvPlayerScore: TextView = itemView.findViewById(R.id.tvPlayerScore)

        fun bind(player: Player, position: Int) {
            tvPosition.text = position.toString()
            tvPlayerName.text = player.name
            tvPlayerLevel.text = player.level


            val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault())
                .format(player.score)
            tvPlayerScore.text = formattedScore


            ivAvatar.setImageResource(R.drawable.ic_person)


            itemView.setOnClickListener {
                onPlayerClick(player)
            }
        }
    }
}