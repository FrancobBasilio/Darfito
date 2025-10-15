package com.tuusuario.darfito.data.dao

import android.content.ContentValues
import android.content.Context
import com.tuusuario.darfito.data.AppDatabaseHelper
import com.tuusuario.darfito.model.GameDifficulty
import com.tuusuario.darfito.model.Question

class QuestionDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    /**
     * INSERTAR NUEVA PREGUNTA
     */
    fun insertar(question: Question): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("text", question.text)
            put("options", question.options.joinToString("|||"))
            put("correctAnswer", question.correctAnswer)
            put("category", question.category)
            put("difficulty", question.difficulty.name)
        }
        return db.insert("question", null, valores)
    }

    /**
     * OBTENER PREGUNTAS POR DIFICULTAD
     */
    fun obtenerPorDificultad(difficulty: GameDifficulty): List<Question> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "question",
            null,
            "difficulty = ?",
            arrayOf(difficulty.name),
            null, null, null
        )
        val preguntas = mutableListOf<Question>()

        while (cursor.moveToNext()) {
            val optionsString = cursor.getString(cursor.getColumnIndexOrThrow("options"))
            val options = optionsString.split("|||")

            preguntas.add(
                Question(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_question")),
                    text = cursor.getString(cursor.getColumnIndexOrThrow("text")),
                    options = options,
                    correctAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("correctAnswer")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    difficulty = GameDifficulty.valueOf(
                        cursor.getString(cursor.getColumnIndexOrThrow("difficulty"))
                    )
                )
            )
        }
        cursor.close()
        return preguntas
    }

    /**
     * OBTENER TODAS LAS PREGUNTAS
     */
    fun obtenerTodas(): List<Question> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("question", null, null, null, null, null, null)
        val preguntas = mutableListOf<Question>()

        while (cursor.moveToNext()) {
            val optionsString = cursor.getString(cursor.getColumnIndexOrThrow("options"))
            val options = optionsString.split("|||")

            preguntas.add(
                Question(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_question")),
                    text = cursor.getString(cursor.getColumnIndexOrThrow("text")),
                    options = options,
                    correctAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("correctAnswer")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    difficulty = GameDifficulty.valueOf(
                        cursor.getString(cursor.getColumnIndexOrThrow("difficulty"))
                    )
                )
            )
        }
        cursor.close()
        return preguntas
    }

    /**
     * OBTENER PREGUNTAS ALEATORIAS POR DIFICULTAD
     */
    fun obtenerAleatoriasPorDificultad(difficulty: GameDifficulty, cantidad: Int): List<Question> {
        val todasLasPreguntas = obtenerPorDificultad(difficulty)
        return todasLasPreguntas.shuffled().take(cantidad)
    }

    /**
     * ELIMINAR PREGUNTA
     */
    fun eliminar(questionId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("question", "id_question = ?", arrayOf(questionId.toString()))
    }

    /**
     * ACTUALIZAR PREGUNTA
     */
    fun actualizar(question: Question): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("text", question.text)
            put("options", question.options.joinToString("|||"))
            put("correctAnswer", question.correctAnswer)
            put("category", question.category)
            put("difficulty", question.difficulty.name)
        }
        return db.update("question", valores, "id_question = ?", arrayOf(question.id.toString()))
    }

    /**
     * INICIALIZAR BASE DE DATOS CON PREGUNTAS
     */
    fun inicializarPreguntas() {
        if (obtenerTodas().isEmpty()) {
            // ========== PREGUNTAS FÁCILES (10) ==========
            insertar(Question(0, "¿Cuál es la capital de Francia?",
                listOf("París", "Londres", "Madrid", "Roma"), 0, "Geografía", GameDifficulty.EASY))

            insertar(Question(0, "¿Cuántos continentes hay?",
                listOf("5", "6", "7", "8"), 2, "Geografía", GameDifficulty.EASY))

            insertar(Question(0, "¿Cuál es el planeta más grande del sistema solar?",
                listOf("Tierra", "Marte", "Júpiter", "Saturno"), 2, "Ciencia", GameDifficulty.EASY))

            insertar(Question(0, "¿En qué año llegó el hombre a la Luna?",
                listOf("1967", "1968", "1969", "1970"), 2, "Historia", GameDifficulty.EASY))

            insertar(Question(0, "¿Cuál es el océano más grande?",
                listOf("Atlántico", "Pacífico", "Índico", "Ártico"), 1, "Geografía", GameDifficulty.EASY))

            insertar(Question(0, "¿Cuántos lados tiene un hexágono?",
                listOf("5", "6", "7", "8"), 1, "Matemáticas", GameDifficulty.EASY))

            insertar(Question(0, "¿Cuál es el animal más rápido del mundo?",
                listOf("León", "Guepardo", "Águila", "Halcón peregrino"), 3, "Naturaleza", GameDifficulty.EASY))

            insertar(Question(0, "¿En qué país se encuentra Machu Picchu?",
                listOf("Brasil", "Chile", "Perú", "Ecuador"), 2, "Geografía", GameDifficulty.EASY))

            insertar(Question(0, "¿Cuál es el río más largo del mundo?",
                listOf("Nilo", "Amazonas", "Yangtsé", "Misisipi"), 1, "Geografía", GameDifficulty.EASY))

            insertar(Question(0, "¿Cuántos huesos tiene el cuerpo humano adulto?",
                listOf("206", "208", "210", "212"), 0, "Ciencia", GameDifficulty.EASY))

            // ========== PREGUNTAS INTERMEDIAS (15) ==========
            insertar(Question(0, "¿Quién escribió 'Cien años de soledad'?",
                listOf("Mario Vargas Llosa", "Gabriel García Márquez", "Jorge Luis Borges", "Pablo Neruda"), 1, "Literatura", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Cuál es la fórmula química del agua?",
                listOf("H2O", "CO2", "NaCl", "CH4"), 0, "Ciencia", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿En qué año cayó el Muro de Berlín?",
                listOf("1987", "1988", "1989", "1990"), 2, "Historia", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Cuál es la montaña más alta del mundo?",
                listOf("K2", "Everest", "Kangchenjunga", "Makalu"), 1, "Geografía", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Quién pintó 'La Mona Lisa'?",
                listOf("Van Gogh", "Picasso", "Leonardo da Vinci", "Michelangelo"), 2, "Arte", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Cuál es el elemento químico más abundante en el universo?",
                listOf("Oxígeno", "Carbono", "Hidrógeno", "Helio"), 2, "Ciencia", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿En qué ciudad se encuentra el Coliseo Romano?",
                listOf("Atenas", "Roma", "Florencia", "Venecia"), 1, "Historia", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Cuál es la velocidad de la luz?",
                listOf("300,000 km/s", "150,000 km/s", "450,000 km/s", "600,000 km/s"), 0, "Física", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Quién desarrolló la teoría de la relatividad?",
                listOf("Isaac Newton", "Albert Einstein", "Galileo Galilei", "Stephen Hawking"), 1, "Ciencia", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Cuál es el país más pequeño del mundo?",
                listOf("Mónaco", "San Marino", "Vaticano", "Liechtenstein"), 2, "Geografía", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿En qué año se descubrió América?",
                listOf("1490", "1492", "1494", "1496"), 1, "Historia", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Cuántos jugadores tiene un equipo de fútbol en el campo?",
                listOf("9", "10", "11", "12"), 2, "Deportes", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Cuál es el metal más abundante en la corteza terrestre?",
                listOf("Hierro", "Cobre", "Aluminio", "Oro"), 2, "Ciencia", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿Quién fue el primer presidente de Estados Unidos?",
                listOf("Thomas Jefferson", "George Washington", "Abraham Lincoln", "John Adams"), 1, "Historia", GameDifficulty.MEDIUM))

            insertar(Question(0, "¿En qué continente se encuentra Egipto?",
                listOf("Asia", "África", "Europa", "Medio Oriente"), 1, "Geografía", GameDifficulty.MEDIUM))

            // ========== PREGUNTAS DIFÍCILES (20) ==========
            insertar(Question(0, "¿Cuál es la constante de Planck aproximadamente?",
                listOf("6.626 × 10⁻³⁴ J⋅s", "9.109 × 10⁻³¹ kg", "1.602 × 10⁻¹⁹ C", "2.998 × 10⁸ m/s"), 0, "Física", GameDifficulty.HARD))

            insertar(Question(0, "¿Quién fue el primer emperador romano?",
                listOf("Julio César", "Marco Antonio", "Augusto", "Nerón"), 2, "Historia", GameDifficulty.HARD))

            insertar(Question(0, "¿En qué año se publicó 'El origen de las especies'?",
                listOf("1859", "1865", "1871", "1882"), 0, "Ciencia", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuál es la capital de Burkina Faso?",
                listOf("Uagadugú", "Bamako", "Niamey", "N'Djamena"), 0, "Geografía", GameDifficulty.HARD))

            insertar(Question(0, "¿Quién compuso 'Las cuatro estaciones'?",
                listOf("Bach", "Mozart", "Vivaldi", "Beethoven"), 2, "Música", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuál es el número atómico del oro?",
                listOf("77", "78", "79", "80"), 2, "Química", GameDifficulty.HARD))

            insertar(Question(0, "¿En qué batalla fue derrotado Napoleón definitivamente?",
                listOf("Austerlitz", "Waterloo", "Leipzig", "Borodino"), 1, "Historia", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuál es la distancia aproximada de la Tierra al Sol?",
                listOf("150 millones km", "93 millones millas", "1 UA", "Todas son correctas"), 3, "Astronomía", GameDifficulty.HARD))

            insertar(Question(0, "¿Quién escribió 'Ulises'?",
                listOf("James Joyce", "Virginia Woolf", "T.S. Eliot", "Ernest Hemingway"), 0, "Literatura", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuál es la partícula subatómica con carga negativa?",
                listOf("Protón", "Neutrón", "Electrón", "Positrón"), 2, "Física", GameDifficulty.HARD))

            insertar(Question(0, "¿En qué año comenzó la Primera Guerra Mundial?",
                listOf("1912", "1913", "1914", "1915"), 2, "Historia", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuál es el idioma más hablado en el mundo?",
                listOf("Inglés", "Mandarín", "Español", "Hindi"), 1, "Cultura", GameDifficulty.HARD))

            insertar(Question(0, "¿Quién descubrió la penicilina?",
                listOf("Louis Pasteur", "Alexander Fleming", "Marie Curie", "Robert Koch"), 1, "Ciencia", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuál es el río más caudaloso del mundo?",
                listOf("Nilo", "Amazonas", "Congo", "Yangtsé"), 1, "Geografía", GameDifficulty.HARD))

            insertar(Question(0, "¿En qué año se fundó la ONU?",
                listOf("1943", "1944", "1945", "1946"), 2, "Historia", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuál es el hueso más largo del cuerpo humano?",
                listOf("Tibia", "Fémur", "Húmero", "Radio"), 1, "Anatomía", GameDifficulty.HARD))

            insertar(Question(0, "¿Quién pintó 'La última cena'?",
                listOf("Rafael", "Leonardo da Vinci", "Michelangelo", "Donatello"), 1, "Arte", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuál es la capital de Australia?",
                listOf("Sídney", "Melbourne", "Canberra", "Perth"), 2, "Geografía", GameDifficulty.HARD))

            insertar(Question(0, "¿En qué año se inventó Internet?",
                listOf("1969", "1975", "1983", "1991"), 0, "Tecnología", GameDifficulty.HARD))

            insertar(Question(0, "¿Cuántos elementos hay en la tabla periódica?",
                listOf("108", "112", "118", "120"), 2, "Química", GameDifficulty.HARD))
        }
    }
}