package com.tuusuario.darfito.model

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val category: String? = null,
    val difficulty: String? = null
)