package com.tuusuario.darfito.data.dao

import android.content.ContentValues
import android.content.Context
import com.tuusuario.darfito.data.AppDatabaseHelper
import com.tuusuario.darfito.model.Amigo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AmigoDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    /**
     * AGREGAR NUEVO AMIGO (cuando se acepta una solicitud)
     */
    fun agregar(usuario1Id: Int, usuario2Id: Int): Long {
        val db = dbHelper.writableDatabase
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val valores = ContentValues().apply {
            put("id_usuario_1", usuario1Id)
            put("id_usuario_2", usuario2Id)
            put("fecha_amistad", fechaActual)
        }

        return db.insert("amigo", null, valores)
    }

    /**
     * OBTENER TODOS LOS AMIGOS DE UN USUARIO
     * Retorna lista de IDs de usuarios amigos
     */
    fun obtenerAmigos(usuarioId: Int): List<Int> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "amigo",
            null,
            "id_usuario_1 = ? OR id_usuario_2 = ?",
            arrayOf(usuarioId.toString(), usuarioId.toString()),
            null, null, "fecha_amistad DESC"
        )

        val amigosIds = mutableListOf<Int>()

        while (cursor.moveToNext()) {
            val usuario1Id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario_1"))
            val usuario2Id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario_2"))

            // Agregar el ID del amigo (el que no es el usuario actual)
            if (usuario1Id == usuarioId) {
                amigosIds.add(usuario2Id)
            } else {
                amigosIds.add(usuario1Id)
            }
        }
        cursor.close()
        return amigosIds
    }

    /**
     * OBTENER INFORMACIÓN COMPLETA DE AMISTADES
     */
    fun obtenerAmistades(usuarioId: Int): List<Amigo> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "amigo",
            null,
            "id_usuario_1 = ? OR id_usuario_2 = ?",
            arrayOf(usuarioId.toString(), usuarioId.toString()),
            null, null, "fecha_amistad DESC"
        )

        val amistades = mutableListOf<Amigo>()

        while (cursor.moveToNext()) {
            amistades.add(
                Amigo(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_amistad")),
                    usuario1Id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario_1")),
                    usuario2Id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario_2")),
                    fechaAmistad = cursor.getString(cursor.getColumnIndexOrThrow("fecha_amistad"))
                )
            )
        }
        cursor.close()
        return amistades
    }

    /**
     * ELIMINAR AMISTAD
     */
    fun eliminar(usuario1Id: Int, usuario2Id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            "amigo",
            "(id_usuario_1 = ? AND id_usuario_2 = ?) OR (id_usuario_1 = ? AND id_usuario_2 = ?)",
            arrayOf(
                usuario1Id.toString(), usuario2Id.toString(),
                usuario2Id.toString(), usuario1Id.toString()
            )
        )

        return rowsDeleted > 0
    }

    /**
     * VERIFICAR SI DOS USUARIOS SON AMIGOS
     */
    fun sonAmigos(usuario1Id: Int, usuario2Id: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "amigo",
            arrayOf("id_amistad"),
            "(id_usuario_1 = ? AND id_usuario_2 = ?) OR (id_usuario_1 = ? AND id_usuario_2 = ?)",
            arrayOf(
                usuario1Id.toString(), usuario2Id.toString(),
                usuario2Id.toString(), usuario1Id.toString()
            ),
            null, null, null
        )

        val sonAmigos = cursor.count > 0
        cursor.close()
        return sonAmigos
    }

    /**
     * CONTAR AMIGOS DE UN USUARIO
     */
    fun contarAmigos(usuarioId: Int): Int {
        return obtenerAmigos(usuarioId).size
    }
}