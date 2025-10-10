package com.tuusuario.darfito

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.tuusuario.darfito.R
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trivia_game)

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
    }

    private fun initializeGame() {
        tvDifficulty.text = difficulty.displayName.uppercase()
        tvScore.text = currentScore.toString()

        remainingTimeInSeconds = timeLimit

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
        } else {

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
        return when (difficulty) {
            GameDifficulty.EASY -> getEasyQuestions()
            GameDifficulty.MEDIUM -> getMediumQuestions()
            GameDifficulty.HARD -> getHardQuestions()
        }
    }

    private fun getEasyQuestions(): List<Question> {
        return listOf(
            Question("¿Cuál es la capital de Francia?",
                listOf("París", "Londres", "Madrid", "Roma"), 0),
            Question("¿Cuántos continentes hay?",
                listOf("5", "6", "7", "8"), 2),
            Question("¿Cuál es el planeta más grande del sistema solar?",
                listOf("Tierra", "Marte", "Júpiter", "Saturno"), 2),
            Question("¿En qué año llegó el hombre a la Luna?",
                listOf("1967", "1968", "1969", "1970"), 2),
            Question("¿Cuál es el océano más grande?",
                listOf("Atlántico", "Pacífico", "Índico", "Ártico"), 1),
            Question("¿Cuántos lados tiene un hexágono?",
                listOf("5", "6", "7", "8"), 1),
            Question("¿Cuál es el animal más rápido del mundo?",
                listOf("León", "Guepardo", "Águila", "Halcón peregrino"), 3),
            Question("¿En qué país se encuentra Machu Picchu?",
                listOf("Brasil", "Chile", "Perú", "Ecuador"), 2),
            Question("¿Cuál es el río más largo del mundo?",
                listOf("Nilo", "Amazonas", "Yangtsé", "Misisipi"), 1),
            Question("¿Cuántos huesos tiene el cuerpo humano adulto?",
                listOf("206", "208", "210", "212"), 0)
        )
    }

    private fun getMediumQuestions(): List<Question> {
        return listOf(
            Question("¿Quién escribió 'Cien años de soledad'?",
                listOf("Mario Vargas Llosa", "Gabriel García Márquez", "Jorge Luis Borges", "Pablo Neruda"), 1),
            Question("¿Cuál es la fórmula química del agua?",
                listOf("H2O", "CO2", "NaCl", "CH4"), 0),
            Question("¿En qué año cayó el Muro de Berlín?",
                listOf("1987", "1988", "1989", "1990"), 2),
            Question("¿Cuál es la montaña más alta del mundo?",
                listOf("K2", "Everest", "Kangchenjunga", "Makalu"), 1),
            Question("¿Quién pintó 'La Mona Lisa'?",
                listOf("Van Gogh", "Picasso", "Leonardo da Vinci", "Michelangelo"), 2),
            Question("¿Cuál es el elemento químico más abundante en el universo?",
                listOf("Oxígeno", "Carbono", "Hidrógeno", "Helio"), 2),
            Question("¿En qué ciudad se encuentra el Coliseo Romano?",
                listOf("Atenas", "Roma", "Florencia", "Venecia"), 1),
            Question("¿Cuál es la velocidad de la luz?",
                listOf("300,000 km/s", "150,000 km/s", "450,000 km/s", "600,000 km/s"), 0),
            Question("¿Quién desarrolló la teoría de la relatividad?",
                listOf("Isaac Newton", "Albert Einstein", "Galileo Galilei", "Stephen Hawking"), 1),
            Question("¿Cuál es el país más pequeño del mundo?",
                listOf("Mónaco", "San Marino", "Vaticano", "Liechtenstein"), 2)
        )
    }

    private fun getHardQuestions(): List<Question> {
        return listOf(
            Question("¿Cuál es la constante de Planck?",
                listOf("6.626 × 10⁻³⁴ J⋅s", "9.109 × 10⁻³¹ kg", "1.602 × 10⁻¹⁹ C", "2.998 × 10⁸ m/s"), 0),
            Question("¿Quién fue el primer emperador romano?",
                listOf("Julio César", "Marco Antonio", "Augusto", "Nerón"), 2),
            Question("¿En qué año se publicó 'El origen de las especies'?",
                listOf("1859", "1865", "1871", "1882"), 0),
            Question("¿Cuál es la capital de Burkina Faso?",
                listOf("Uagadugú", "Bamako", "Niamey", "N'Djamena"), 0),
            Question("¿Quién compuso 'Las cuatro estaciones'?",
                listOf("Bach", "Mozart", "Vivaldi", "Beethoven"), 2),
            Question("¿Cuál es el número atómico del oro?",
                listOf("77", "78", "79", "80"), 2),
            Question("¿En qué batalla fue derrotado Napoleón definitivamente?",
                listOf("Austerlitz", "Waterloo", "Leipzig", "Borodino"), 1),
            Question("¿Cuál es la distancia aproximada de la Tierra al Sol?",
                listOf("150 millones km", "93 millones millas", "1 UA", "Todas son correctas"), 3),
            Question("¿Quién escribió 'Ulises'?",
                listOf("James Joyce", "Virginia Woolf", "T.S. Eliot", "Ernest Hemingway"), 0),
            Question("¿Cuál es la partícula subatómica con carga negativa?",
                listOf("Protón", "Neutrón", "Electrón", "Positrón"), 2)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}