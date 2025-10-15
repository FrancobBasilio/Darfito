package com.tuusuario.darfito.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuusuario.darfito.R
import com.tuusuario.darfito.model.Player
import com.tuusuario.darfito.model.Usuario
import java.text.NumberFormat
import java.util.Locale

class AmigoAdapter(
    private val onJugar: (Usuario, Player?) -> Unit,
    private val onEliminar: (Usuario) -> Unit
) : RecyclerView.Adapter<AmigoAdapter.AmigoViewHolder>() {

    private var amigos: List<Pair<Usuario, Player?>> = emptyList()

    fun updateAmigos(newAmigos: List<Pair<Usuario, Player?>>) {
        amigos = newAmigos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_amigo, parent, false)
        return AmigoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        val (usuario, player) = amigos[position]
        holder.bind(usuario, player)
    }

    override fun getItemCount() = amigos.size

    inner class AmigoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.ivAmigoAvatar)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvAmigoNombre)
        private val tvScore: TextView = itemView.findViewById(R.id.tvAmigoScore)
        private val tvLevel: TextView = itemView.findViewById(R.id.tvAmigoLevel)
        private val btnJugar: MaterialButton = itemView.findViewById(R.id.btnJugar)
        private val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(usuario: Usuario, player: Player?) {
            tvNombre.text = "${usuario.nombres} ${usuario.apellidos}"

            // Mostrar información del player
            player?.let {
                val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault())
                    .format(it.score)
                tvScore.text = "Score: $formattedScore"
                tvLevel.text = it.level
                ivAvatar.setImageResource(it.avatarResId)
            } ?: run {
                tvScore.text = "Score: 0"
                tvLevel.text = "Nivel Básico"
                ivAvatar.setImageResource(R.drawable.ic_person)
            }

            btnJugar.setOnClickListener {
                onJugar(usuario, player)
            }

            btnEliminar.setOnClickListener {
                onEliminar(usuario)
            }
        }
    }
}