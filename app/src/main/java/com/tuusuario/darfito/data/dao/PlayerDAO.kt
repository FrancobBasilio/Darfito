package com.tuusuario.darfito.data.dao

import android.content.ContentValues
import android.content.Context
import com.tuusuario.darfito.data.AppDatabaseHelper
import com.tuusuario.darfito.model.Player

class PlayerDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    /**
     * INSERTAR NUEVO PLAYER
     */
    fun insertar(player: Player): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("id_usuario", player.usuarioId)
            put("score", player.score)
            put("level", player.level)
            put("avatarResId", player.avatarResId)
        }
        return db.insert("player", null, valores)
    }

    /**
     * BUSCAR PLAYER POR USUARIO ID
     */
    fun buscarPorUsuarioId(usuarioId: Int): Player? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "player",
            null,
            "id_usuario = ?",
            arrayOf(usuarioId.toString()),
            null, null, null
        )

        var player: Player? = null
        if (cursor.moveToFirst()) {
            player = Player(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id_player")).toString(),
                usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                score = cursor.getInt(cursor.getColumnIndexOrThrow("score")),
                level = cursor.getString(cursor.getColumnIndexOrThrow("level")),
                avatarResId = cursor.getInt(cursor.getColumnIndexOrThrow("avatarResId"))
            )
        }
        cursor.close()
        return player
    }

    /**
     * OBTENER TODOS LOS PLAYERS (PARA RANKING)
     */
    fun obtenerTodos(): List<Player> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "player",
            null,
            null,
            null,
            null,
            null,
            "score DESC"  // Ordenado por score descendente
        )
        val players = mutableListOf<Player>()

        while (cursor.moveToNext()) {
            players.add(
                Player(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_player")).toString(),
                    usuarioId = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                    score = cursor.getInt(cursor.getColumnIndexOrThrow("score")),
                    level = cursor.getString(cursor.getColumnIndexOrThrow("level")),
                    avatarResId = cursor.getInt(cursor.getColumnIndexOrThrow("avatarResId"))
                )
            )
        }
        cursor.close()
        return players
    }

    /**
     * ACTUALIZAR PLAYER (SCORE, LEVEL, AVATAR)
     */
    fun actualizar(player: Player): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("score", player.score)
            put("level", player.level)
            put("avatarResId", player.avatarResId)
        }
        return db.update("player", valores, "id_player = ?", arrayOf(player.id))
    }

    /**
     * ACTUALIZAR SOLO EL SCORE
     */
    fun actualizarScore(playerId: String, nuevoScore: Int): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("score", nuevoScore)
        }
        return db.update("player", valores, "id_player = ?", arrayOf(playerId))
    }

    /**
     * ELIMINAR PLAYER
     */
    fun eliminar(playerId: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete("player", "id_player = ?", arrayOf(playerId))
    }

    /**
     * VERIFICAR SI YA EXISTE PLAYER PARA UN USUARIO
     */
    fun existePlayerParaUsuario(usuarioId: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "player",
            arrayOf("id_player"),
            "id_usuario = ?",
            arrayOf(usuarioId.toString()),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }
}