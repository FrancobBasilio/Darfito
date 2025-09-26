package com.tuusuario.darfito.model

data class Player(
    val id: String,
    val name: String,
    val score: Int,
    val level: String,
    val avatarUrl: String? = null
)