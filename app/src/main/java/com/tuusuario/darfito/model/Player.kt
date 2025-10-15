package com.tuusuario.darfito.model

data class Player(
    val id: String,
    val usuarioId: Int,
    val score: Int,
    val level: String,
    val avatarResId: Int
)