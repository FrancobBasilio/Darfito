package com.tuusuario.darfito


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.tuusuario.darfito.R
import com.tuusuario.darfito.data.dao.QuestionDAO
import com.tuusuario.darfito.model.GameDifficulty
import com.tuusuario.darfito.model.Question

class TriviaGameActivity : AppCompatActivity() {

    // Views
    private lateinit var btnPause: ImageButton
    private lateinit var tvDifficulty: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvQuestionProgress: TextView
    private lateinit var tvTimeRemaining: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTimer: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var cardOptionA: CardView
    private lateinit var cardOptionB: CardView
    private lateinit var cardOptionC: CardView
    private lateinit var cardOptionD: CardView
    private lateinit var tvOptionA: TextView
    private lateinit var tvOptionB: TextView
    private lateinit var tvOptionC: TextView
    private lateinit var tvOptionD: TextView

    // Variables de juego
    private var difficulty: GameDifficulty = GameDifficulty.EASY
    private var timeLimit: Int = 30
    private var pointsPerQuestion: Int = 10
    private var totalQuestions: Int = 10
    private var currentQuestionIndex: Int = 0
    private var currentScore: Int = 0
    private var correctAnswers: Int = 0
    private var timer: CountDownTimer? = null
    private var questions: List<Question> = emptyList()
    private var isAnswerSelected = false
    private var remainingTimeInSeconds: Int = 0
    private val timeBonusForCorrectAnswer: Int = 5
    private var usuarioId: Int = -1  // ✅ AGREGAR ESTO

    // DAO
    private lateinit var questionDAO: QuestionDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trivia_game)

        // Inicializar DAO
        questionDAO = QuestionDAO(this)

        // Inicializar preguntas en BD (solo la primera vez)
        questionDAO.inicializarPreguntas()

        initViews()
        getGameParameters()
        initializeGame()
        setupClickListeners()
    }

    private fun initViews() {
        btnPause = findViewById(R.id.btnPause)
        tvDifficulty = findViewById(R.id.tvDifficulty)
        tvScore = findViewById(R.id.tvScore)
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress)
        tvTimeRemaining = findViewById(R.id.tvTimeRemaining)
        progressBar = findViewById(R.id.progressBar)
        tvTimer = findViewById(R.id.tvTimer)
        tvQuestion = findViewById(R.id.tvQuestion)
        cardOptionA = findViewById(R.id.cardOptionA)
        cardOptionB = findViewById(R.id.cardOptionB)
        cardOptionC = findViewById(R.id.cardOptionC)
        cardOptionD = findViewById(R.id.cardOptionD)
        tvOptionA = findViewById(R.id.tvOptionA)
        tvOptionB = findViewById(R.id.tvOptionB)
        tvOptionC = findViewById(R.id.tvOptionC)
        tvOptionD = findViewById(R.id.tvOptionD)
    }

    private fun getGameParameters() {
        val difficultyName = intent.getStringExtra("DIFFICULTY") ?: "EASY"
        difficulty = GameDifficulty.valueOf(difficultyName)
        timeLimit = intent.getIntExtra("TIME_LIMIT", 30)
        pointsPerQuestion = intent.getIntExtra("POINTS_PER_QUESTION", 10)
        totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 10)

        usuarioId = intent.getIntExtra("USUARIO_ID", -1)

        // DEBUG
        Log.d("TRIVIA_DEBUG", "USUARIO_ID recibido: $usuarioId")
        // DEBUG
        Toast.makeText(this, "Trivia - usuarioId: $usuarioId", Toast.LENGTH_LONG).show()
    }

    private fun initializeGame() {
        tvDifficulty.text = difficulty.displayName.uppercase()
        tvScore.text = currentScore.toString()
        remainingTimeInSeconds = timeLimit

        // Cargar preguntas desde la BD usando DAO
        questions = loadQuestionsForDifficulty(difficulty)

        showCurrentQuestion()
    }

    private fun setupClickListeners() {
        btnPause.setOnClickListener {
            pauseGame()
        }

        cardOptionA.setOnClickListener { selectAnswer(0) }
        cardOptionB.setOnClickListener { selectAnswer(1) }
        cardOptionC.setOnClickListener { selectAnswer(2) }
        cardOptionD.setOnClickListener { selectAnswer(3) }
    }

    private fun showCurrentQuestion() {
        if (currentQuestionIndex >= questions.size) {
            endGame()
            return
        }

        val question = questions[currentQuestionIndex]
        isAnswerSelected = false

        tvQuestion.text = question.text
        tvOptionA.text = question.options[0]
        tvOptionB.text = question.options[1]
        tvOptionC.text = question.options[2]
        tvOptionD.text = question.options[3]

        tvQuestionProgress.text = "Pregunta ${currentQuestionIndex + 1} de $totalQuestions"

        resetOptionColors()
        startTimer()
    }

    private fun startTimer() {
        timer?.cancel()

        if (remainingTimeInSeconds <= 0) {
            endGame()
            return
        }

        timer = object : CountDownTimer((remainingTimeInSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                remainingTimeInSeconds = secondsRemaining

                tvTimer.text = secondsRemaining.toString()
                tvTimeRemaining.text = "⏱️ ${secondsRemaining}s"

                val progressPercentage = ((secondsRemaining.toFloat() / timeLimit) * 100).toInt()
                progressBar.progress = progressPercentage

                if (secondsRemaining <= 5) {
                    tvTimer.setTextColor(ContextCompat.getColor(this@TriviaGameActivity, android.R.color.holo_red_light))
                } else {
                    tvTimer.setTextColor(ContextCompat.getColor(this@TriviaGameActivity, android.R.color.white))
                }
            }

            override fun onFinish() {
                progressBar.progress = 0
                if (!isAnswerSelected) {
                    showCorrectAnswer()
                    endGame()
                }
            }
        }.start()
    }

    private fun selectAnswer(selectedIndex: Int) {
        if (isAnswerSelected) return

        isAnswerSelected = true
        timer?.cancel()

        val question = questions[currentQuestionIndex]
        val isCorrect = selectedIndex == question.correctAnswer

        showAnswerFeedback(selectedIndex, question.correctAnswer, isCorrect)

        if (isCorrect) {
            correctAnswers++
            currentScore += pointsPerQuestion
            tvScore.text = currentScore.toString()
            remainingTimeInSeconds += timeBonusForCorrectAnswer
        }

        tvTimer.postDelayed({ moveToNextQuestion() }, 2000)
    }

    private fun showAnswerFeedback(selectedIndex: Int, correctIndex: Int, isCorrect: Boolean) {
        val cards = listOf(cardOptionA, cardOptionB, cardOptionC, cardOptionD)

        cards[correctIndex].setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.correct_answer_color)
        )

        if (!isCorrect) {
            cards[selectedIndex].setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.incorrect_answer_color)
            )
        }
    }

    private fun showCorrectAnswer() {
        val question = questions[currentQuestionIndex]
        val cards = listOf(cardOptionA, cardOptionB, cardOptionC, cardOptionD)

        cards[question.correctAnswer].setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.correct_answer_color)
        )
    }

    private fun resetOptionColors() {
        val cards = listOf(cardOptionA, cardOptionB, cardOptionC, cardOptionD)
        val defaultColor = ContextCompat.getColor(this, R.color.option_default_color)

        cards.forEach { card ->
            card.setCardBackgroundColor(defaultColor)
        }
    }

    private fun moveToNextQuestion() {
        currentQuestionIndex++
        showCurrentQuestion()
    }

    private fun endGame() {
        timer?.cancel()

        val intent = Intent(this, GameResultActivity::class.java).apply {
            putExtra("SCORE", currentScore)
            putExtra("CORRECT_ANSWERS", correctAnswers)
            putExtra("TOTAL_QUESTIONS", totalQuestions)
            putExtra("DIFFICULTY", difficulty.name)
            putExtra("USUARIO_ID", usuarioId)
        }
        startActivity(intent)
        finish()
    }

    private fun pauseGame() {
        timer?.cancel()

        AlertDialog.Builder(this)
            .setTitle("Juego Pausado")
            .setMessage("¿Qué deseas hacer?")
            .setPositiveButton("Continuar") { _, _ ->
                startTimer()
            }
            .setNegativeButton("Salir del Juego") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun loadQuestionsForDifficulty(difficulty: GameDifficulty): List<Question> {
        // Obtener preguntas aleatorias de la BD según dificultad
        return questionDAO.obtenerAleatoriasPorDificultad(difficulty, totalQuestions)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}