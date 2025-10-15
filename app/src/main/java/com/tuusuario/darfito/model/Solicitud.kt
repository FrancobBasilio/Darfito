package com.tuusuario.darfito.model

data class Solicitud(
    val id: Int,
    val usuarioEnviaId: Int,
    val usuarioRecibeId: Int,
    val estado: String, // "pendiente", "aceptada", "rechazada"
    val fechaSolicitud: String
)
