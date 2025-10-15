package com.tuusuario.darfito.ui

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
import com.tuusuario.darfito.adapter.SolicitudAdapter
import com.tuusuario.darfito.data.dao.AmigoDAO
import com.tuusuario.darfito.data.dao.PlayerDAO
import com.tuusuario.darfito.data.dao.SolicitudDAO
import com.tuusuario.darfito.data.dao.UsuarioDAO
import com.tuusuario.darfito.model.Player
import com.tuusuario.darfito.model.Solicitud
import com.tuusuario.darfito.model.Usuario

class SolicitudFragment : Fragment() {

    private lateinit var rvSolicitudes: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var solicitudAdapter: SolicitudAdapter

    // DAOs
    private lateinit var solicitudDAO: SolicitudDAO
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var playerDAO: PlayerDAO
    private lateinit var amigoDAO: AmigoDAO

    private var usuarioId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_solicitud, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar DAOs
        solicitudDAO = SolicitudDAO(requireContext())
        usuarioDAO = UsuarioDAO(requireContext())
        playerDAO = PlayerDAO(requireContext())
        amigoDAO = AmigoDAO(requireContext())

        // Obtener usuario ID
        usuarioId = (activity as? HomeActivity)?.obtenerUsuarioId() ?: -1

        initViews(view)
        setupRecyclerView()
        cargarSolicitudes()
    }

    private fun initViews(view: View) {
        rvSolicitudes = view.findViewById(R.id.rvSolicitudes)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
    }

    private fun setupRecyclerView() {
        solicitudAdapter = SolicitudAdapter(
            onAceptar = { solicitud, usuario, player ->
                mostrarDialogoConfirmacion(
                    "Aceptar Solicitud",
                    "¿Deseas agregar a ${usuario.nombres} como amigo?",
                    onConfirm = { aceptarSolicitud(solicitud, usuario) }
                )
            },
            onRechazar = { solicitud ->
                mostrarDialogoConfirmacion(
                    "Rechazar Solicitud",
                    "¿Estás seguro de rechazar esta solicitud?",
                    onConfirm = { rechazarSolicitud(solicitud) }
                )
            }
        )

        rvSolicitudes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = solicitudAdapter
        }
    }

    private fun cargarSolicitudes() {
        if (usuarioId == -1) {
            Toast.makeText(requireContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener solicitudes pendientes
        val solicitudesPendientes = solicitudDAO.obtenerSolicitudesPendientes(usuarioId)

        // Cargar información completa de cada solicitud
        val solicitudesCompletas = solicitudesPendientes.map { solicitud ->
            val usuario = usuarioDAO.obtenerPorId(solicitud.usuarioEnviaId)
            val player = usuario?.let { playerDAO.buscarPorUsuarioId(it.id) }
            Triple(solicitud, usuario ?: Usuario(0, "Usuario", "", "", "", ""), player)
        }

        // Actualizar adapter
        solicitudAdapter.updateSolicitudes(solicitudesCompletas)

        // Mostrar mensaje si no hay solicitudes
        if (solicitudesCompletas.isEmpty()) {
            rvSolicitudes.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
            tvEmptyState.text = "No tienes solicitudes pendientes"
        } else {
            rvSolicitudes.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
        }
    }

    private fun aceptarSolicitud(solicitud: Solicitud, usuario: Usuario) {
        // Aceptar la solicitud en la BD
        val aceptada = solicitudDAO.aceptarSolicitud(solicitud.id)

        if (aceptada) {
            // Crear la amistad
            val amistadCreada = amigoDAO.agregar(solicitud.usuarioRecibeId, solicitud.usuarioEnviaId)

            if (amistadCreada != -1L) {
                Toast.makeText(
                    requireContext(),
                    "¡Ahora son amigos con ${usuario.nombres}!",
                    Toast.LENGTH_SHORT
                ).show()

                // Eliminar la solicitud de la lista
                solicitudDAO.eliminar(solicitud.id)

                // Recargar lista
                cargarSolicitudes()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al crear la amistad",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Error al aceptar la solicitud",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun rechazarSolicitud(solicitud: Solicitud) {
        // Rechazar la solicitud
        val rechazada = solicitudDAO.rechazarSolicitud(solicitud.id)

        if (rechazada) {
            Toast.makeText(
                requireContext(),
                "Solicitud rechazada",
                Toast.LENGTH_SHORT
            ).show()

            // Eliminar de la lista
            solicitudDAO.eliminar(solicitud.id)

            // Recargar lista
            cargarSolicitudes()
        } else {
            Toast.makeText(
                requireContext(),
                "Error al rechazar la solicitud",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun mostrarDialogoConfirmacion(
        titulo: String,
        mensaje: String,
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Sí") { dialog, _ ->
                onConfirm()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        cargarSolicitudes()
    }
}