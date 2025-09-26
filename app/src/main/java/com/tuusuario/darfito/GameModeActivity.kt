package com.tuusuario.darfito

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.androidgamesdk.GameActivity
import com.tuusuario.darfito.model.GameDifficulty

class GameModeActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var cardEasy: CardView
    private lateinit var cardMedium: CardView
    private lateinit var cardHard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_mode)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        cardEasy = findViewById(R.id.cardEasy)
        cardMedium = findViewById(R.id.cardMedium)
        cardHard = findViewById(R.id.cardHard)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        cardEasy.setOnClickListener {
            startGame(GameDifficulty.EASY)
        }

        cardMedium.setOnClickListener {
            startGame(GameDifficulty.MEDIUM)
        }

        cardHard.setOnClickListener {
            startGame(GameDifficulty.HARD)
        }
    }

    private fun startGame(difficulty: GameDifficulty) {
        val intent = Intent(this, TriviaGameActivity::class.java).apply {
            putExtra("DIFFICULTY", difficulty.name)
            putExtra("TIME_LIMIT", difficulty.timeLimit)
            putExtra("POINTS_PER_QUESTION", difficulty.pointsPerQuestion)
            putExtra("TOTAL_QUESTIONS", difficulty.totalQuestions)
        }
        startActivity(intent)

    }
}