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
import com.tuusuario.darfito.model.Solicitud
import com.tuusuario.darfito.model.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SolicitudAdapter(
    private val onAceptar: (Solicitud, Usuario, Player?) -> Unit,
    private val onRechazar: (Solicitud) -> Unit
) : RecyclerView.Adapter<SolicitudAdapter.SolicitudViewHolder>() {

    private var solicitudes: List<Triple<Solicitud, Usuario, Player?>> = emptyList()

    fun updateSolicitudes(newSolicitudes: List<Triple<Solicitud, Usuario, Player?>>) {
        solicitudes = newSolicitudes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_solicitud, parent, false)
        return SolicitudViewHolder(view)
    }

    override fun onBindViewHolder(holder: SolicitudViewHolder, position: Int) {
        val (solicitud, usuario, player) = solicitudes[position]
        holder.bind(solicitud, usuario, player)
    }

    override fun getItemCount() = solicitudes.size

    inner class SolicitudViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.ivSolicitudAvatar)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvSolicitudNombre)
        private val tvFecha: TextView = itemView.findViewById(R.id.tvSolicitudFecha)
        private val btnAceptar: MaterialButton = itemView.findViewById(R.id.btnAceptar)
        private val btnRechazar: MaterialButton = itemView.findViewById(R.id.btnRechazar)

        fun bind(solicitud: Solicitud, usuario: Usuario, player: Player?) {
            tvNombre.text = "${usuario.nombres} ${usuario.apellidos}"
            tvFecha.text = formatearFecha(solicitud.fechaSolicitud)

            // Mostrar avatar del player
            player?.let {
                ivAvatar.setImageResource(it.avatarResId)
            } ?: run {
                ivAvatar.setImageResource(R.drawable.ic_person)
            }

            btnAceptar.setOnClickListener {
                onAceptar(solicitud, usuario, player)
            }

            btnRechazar.setOnClickListener {
                onRechazar(solicitud)
            }
        }

        private fun formatearFecha(fecha: String): String {
            return try {
                val formato = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val fechaSolicitud = formato.parse(fecha)
                val ahora = Date()

                val diferencia = ahora.time - (fechaSolicitud?.time ?: 0)
                val minutos = diferencia / (1000 * 60)
                val horas = diferencia / (1000 * 60 * 60)
                val dias = diferencia / (1000 * 60 * 60 * 24)

                when {
                    minutos < 1 -> "Ahora"
                    minutos < 60 -> "Hace ${minutos}m"
                    horas < 24 -> "Hace ${horas}h"
                    dias < 7 -> "Hace ${dias}d"
                    else -> fecha.substring(0, 10)
                }
            } catch (e: Exception) {
                fecha
            }
        }
    }
}