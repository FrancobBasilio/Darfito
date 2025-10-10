package com.tuusuario.darfito

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.tuusuario.darfito.model.GameDifficulty

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_result)

        initViews()
        getResultData()
        displayResults()
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
            }
            startActivity(intent)
            finish()
        }

        btnViewRanking.setOnClickListener {

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnBackToHome.setOnClickListener {

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}