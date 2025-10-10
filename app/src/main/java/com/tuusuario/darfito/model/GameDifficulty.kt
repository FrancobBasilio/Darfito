package com.tuusuario.darfito.model

enum class GameDifficulty(
    val displayName: String,
    val timeLimit: Int,
    val pointsPerQuestion: Int,
    val totalQuestions: Int,
    val description: String
) {
    EASY(
        displayName = "Fácil",
        timeLimit = 30,
        pointsPerQuestion = 10,
        totalQuestions = 10,
        description = "Preguntas básicas de cultura general"
    ),
    MEDIUM(
        displayName = "Intermedio",
        timeLimit = 20,
        pointsPerQuestion = 25,
        totalQuestions = 15,
        description = "Desafío moderado para mentes curiosas"
    ),
    HARD(
        displayName = "Difícil",
        timeLimit = 15,
        pointsPerQuestion = 50,
        totalQuestions = 20,
        description = "Solo para verdaderos expertos"
    )
}