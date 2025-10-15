package com.tuusuario.darfito

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.tuusuario.darfito.R
import com.tuusuario.darfito.data.dao.PlayerDAO
import com.tuusuario.darfito.model.GameDifficulty
import com.tuusuario.darfito.model.Player

class GameResultActivity : AppCompatActivity() {

    private lateinit var tvResultTitle: TextView
    private lateinit var tvFinalScore: TextView
    private lateinit var tvCorrectAnswers: TextView
    private lateinit var tvAccuracyPercentage: TextView
    private lateinit var tvDifficultyLevel: TextView
    private lateinit var tvAchievements: TextView
    private lateinit var cardAchievements: CardView
    private lateinit var btnPlayAgain: MaterialButton
    private lateinit var btnViewRanking: MaterialButton
    private lateinit var btnBackToHome: MaterialButton

    private var score: Int = 0
    private var correctAnswers: Int = 0
    private var totalQuestions: Int = 0
    private lateinit var difficulty: GameDifficulty
    private var usuarioId: Int = -1

    // DAO
    private lateinit var playerDAO: PlayerDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        // Inicializar DAO
        playerDAO = PlayerDAO(this)

        initViews()
        getResultData()
        displayResults()
        actualizarScoreJugador()
        setupClickListeners()
    }

    private fun initViews() {
        tvResultTitle = findViewById(R.id.tvResultTitle)
        tvFinalScore = findViewById(R.id.tvFinalScore)
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers)
        tvAccuracyPercentage = findViewById(R.id.tvAccuracyPercentage)
        tvDifficultyLevel = findViewById(R.id.tvDifficultyLevel)
        tvAchievements = findViewById(R.id.tvAchievements)
        cardAchievements = findViewById(R.id.cardAchievements)
        btnPlayAgain = findViewById(R.id.btnPlayAgain)
        btnViewRanking = findViewById(R.id.btnViewRanking)
        btnBackToHome = findViewById(R.id.btnBackToHome)
    }

    private fun getResultData() {
        score = intent.getIntExtra("SCORE", 0)
        correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)
        totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)
        val difficultyName = intent.getStringExtra("DIFFICULTY") ?: "EASY"
        difficulty = GameDifficulty.valueOf(difficultyName)
        usuarioId = intent.getIntExtra("USUARIO_ID", -1)

        // DEBUG
        Log.d("RESULT_DEBUG", "USUARIO_ID recibido: $usuarioId")
        Toast.makeText(this, "Result - Usuario ID: $usuarioId, Score: $score", Toast.LENGTH_LONG).show()
    }

    private fun displayResults() {
        tvFinalScore.text = score.toString()
        tvCorrectAnswers.text = "$correctAnswers/$totalQuestions"

        val accuracyPercentage = if (totalQuestions > 0) {
            (correctAnswers * 100) / totalQuestions
        } else 0
        tvAccuracyPercentage.text = "$accuracyPercentage%"

        tvDifficultyLevel.text = difficulty.displayName.uppercase()

        val title = when {
            accuracyPercentage >= 90 -> "🏆 ¡PERFECTO!"
            accuracyPercentage >= 80 -> "🎉 ¡Excelente!"
            accuracyPercentage >= 70 -> "👍 ¡Muy bien!"
            accuracyPercentage >= 60 -> "😊 ¡Bien hecho!"
            else -> "💪 ¡Sigue intentando!"
        }
        tvResultTitle.text = title

        generateAchievements(accuracyPercentage)
    }

    private fun actualizarScoreJugador() {
        if (usuarioId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        val player = playerDAO.buscarPorUsuarioId(usuarioId)

        if (player != null) {
            // Calcular nuevo score
            val nuevoScore = player.score + score

            // Determinar nuevo nivel basado en el score total
            val nuevoNivel = determinarNivel(nuevoScore)

            // Actualizar PLAYER COMPLETO (score + level)
            val playerActualizado = player.copy(
                score = nuevoScore,
                level = nuevoNivel
            )

            val rowsAffected = playerDAO.actualizar(playerActualizado)

            if (rowsAffected > 0) {
                Toast.makeText(
                    this,
                    "Score: $nuevoScore (+$score) - $nuevoNivel",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Si no existe player, crearlo
            val nuevoPlayer = Player(
                id = "0",
                usuarioId = usuarioId,
                score = score,
                level = determinarNivel(score),
                avatarResId = R.drawable.ic_person
            )
            playerDAO.insertar(nuevoPlayer)
            Toast.makeText(this, "Perfil creado. Score: $score", Toast.LENGTH_SHORT).show()
        }
    }
    private fun determinarNivel(score: Int): String {
        return when {
            score >= 500 -> "Nivel Experto"
            score >= 300 -> "Nivel Avanzado"
            score >= 150 -> "Nivel Intermedio"
            else -> "Nivel Básico"
        }
    }

    private fun generateAchievements(accuracy: Int) {
        val achievements = mutableListOf<String>()

        if (accuracy == 100) achievements.add("🎯 Puntuación Perfecta")
        if (accuracy >= 80) achievements.add("🧠 Cerebrito")
        if (correctAnswers >= 5) achievements.add("⚡ Respuestas Correctas")
        if (difficulty == GameDifficulty.HARD) achievements.add("🔥 Desafío Extremo")
        if (score >= 200) achievements.add("💎 Alto Puntaje")

        if (achievements.isEmpty()) {
            achievements.add("🎮 Primer Intento")
            achievements.add("📚 Aprendiz")
        }

        tvAchievements.text = achievements.joinToString("\n")
    }

    private fun setupClickListeners() {
        btnPlayAgain.setOnClickListener {
            val intent = Intent(this, TriviaGameActivity::class.java).apply {
                putExtra("DIFFICULTY", difficulty.name)
                putExtra("TIME_LIMIT", difficulty.timeLimit)
                putExtra("POINTS_PER_QUESTION", difficulty.pointsPerQuestion)
                putExtra("TOTAL_QUESTIONS", difficulty.totalQuestions)
                putExtra("USUARIO_ID", usuarioId)
            }
            startActivity(intent)
            finish()
        }

        btnViewRanking.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("usuario_id", usuarioId)
                // Usar FLAG_ACTIVITY_CLEAR_TOP para volver al Home existente
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        btnBackToHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("usuario_id", usuarioId)
                // Usar FLAG_ACTIVITY_CLEAR_TOP para volver al Home existente
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }
}