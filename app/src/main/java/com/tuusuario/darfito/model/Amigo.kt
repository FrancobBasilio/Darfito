package com.tuusuario.darfito.model

data class Amigo(
    val id: Int,
    val usuario1Id: Int,
    val usuario2Id: Int,
    val fechaAmistad: String
)