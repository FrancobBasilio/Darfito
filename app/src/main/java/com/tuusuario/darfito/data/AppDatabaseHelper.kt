package com.tuusuario.darfito.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context : Context) : SQLiteOpenHelper(context, "trivia.db", null, 2){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE usuario (
            id_usuario INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            nombres TEXT,
            apellidos TEXT,
            correo TEXT,
            clave TEXT,
            genero TEXT
            )
        """.trimIndent())


        db.execSQL("""
            CREATE TABLE player (
            id_player INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            id_usuario INTEGER,
            score INTEGER,
            level TEXT,
            avatarResId INTEGER,
            FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE question (
                id_question INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                text TEXT,
                options TEXT,
                correctAnswer INTEGER,
                category TEXT,
                difficulty TEXT
            )

        """.trimIndent())

        // Tabla de solicitudes de amistad
        db.execSQL("""
            CREATE TABLE solicitud (
                id_solicitud INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                id_usuario_envia INTEGER,
                id_usuario_recibe INTEGER,
                estado TEXT DEFAULT 'pendiente',
                fecha_solicitud TEXT,
                FOREIGN KEY (id_usuario_envia) REFERENCES usuario (id_usuario),
                FOREIGN KEY (id_usuario_recibe) REFERENCES usuario (id_usuario)
            )
        """.trimIndent())

        // Tabla de amigos (cuando se acepta una solicitud)
        db.execSQL("""
            CREATE TABLE amigo (
                id_amistad INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                id_usuario_1 INTEGER,
                id_usuario_2 INTEGER,
                fecha_amistad TEXT,
                FOREIGN KEY (id_usuario_1) REFERENCES usuario (id_usuario),
                FOREIGN KEY (id_usuario_2) REFERENCES usuario (id_usuario)
            )
        """.trimIndent())

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Crear nuevas tablas en versión 2
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS solicitud (
                    id_solicitud INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    id_usuario_envia INTEGER,
                    id_usuario_recibe INTEGER,
                    estado TEXT DEFAULT 'pendiente',
                    fecha_solicitud TEXT,
                    FOREIGN KEY (id_usuario_envia) REFERENCES usuario (id_usuario),
                    FOREIGN KEY (id_usuario_recibe) REFERENCES usuario (id_usuario)
                )
            """.trimIndent())

            db.execSQL("""
                CREATE TABLE IF NOT EXISTS amigo (
                    id_amistad INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    id_usuario_1 INTEGER,
                    id_usuario_2 INTEGER,
                    fecha_amistad TEXT,
                    FOREIGN KEY (id_usuario_1) REFERENCES usuario (id_usuario),
                    FOREIGN KEY (id_usuario_2) REFERENCES usuario (id_usuario)
                )
            """.trimIndent())
        }
    }
}