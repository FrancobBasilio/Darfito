package com.tuusuario.darfito.model

data class Question(
    var id : Int = 0,
    var text: String,
    var options: List<String>,
    var correctAnswer: Int,
    var category: String? = null,
    var difficulty: GameDifficulty
)