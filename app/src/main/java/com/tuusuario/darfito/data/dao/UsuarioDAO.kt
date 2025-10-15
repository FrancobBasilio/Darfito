package com.tuusuario.darfito.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.tuusuario.darfito.data.AppDatabaseHelper
import com.tuusuario.darfito.model.Usuario

class UsuarioDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    /**
     * INSERTAR NUEVO USUARIO
     */
    fun insertar(usuario: Usuario): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombres", usuario.nombres)
            put("apellidos", usuario.apellidos)
            put("correo", usuario.correo)
            put("clave", usuario.clave)
            put("genero", usuario.genero)
        }
        return db.insert("usuario", null, valores)
    }

    /**
     * BUSCAR USUARIO POR CORREO Y CLAVE (LOGIN)
     */
    fun buscarUsuario(correo: String, clave: String): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "usuario",
            null,
            "correo = ? AND clave = ?",
            arrayOf(correo, clave),
            null, null, null
        )

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres")),
                apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                clave = cursor.getString(cursor.getColumnIndexOrThrow("clave")),
                genero = cursor.getString(cursor.getColumnIndexOrThrow("genero"))
            )
        }
        cursor.close()
        return usuario
    }

    /**
     * OBTENER TODOS LOS USUARIOS
     */
    fun obtenerTodos(): List<Usuario> {
        val db = dbHelper.readableDatabase
        val cursor = db.query("usuario", null, null, null, null, null, null)
        val usuarios = mutableListOf<Usuario>()

        while (cursor.moveToNext()) {
            usuarios.add(
                Usuario(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                    nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres")),
                    apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    clave = cursor.getString(cursor.getColumnIndexOrThrow("clave")),
                    genero = cursor.getString(cursor.getColumnIndexOrThrow("genero"))
                )
            )
        }
        cursor.close()
        return usuarios
    }

    /**
     * VERIFICAR SI EXISTE CORREO (PARA REGISTRO)
     */
    fun existeCorreo(correo: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "usuario",
            arrayOf("id_usuario"),
            "correo = ?",
            arrayOf(correo),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    // Agregar este método a UsuarioDAO.kt

    /**
     * OBTENER USUARIO POR ID
     */
    fun obtenerPorId(id: Int): Usuario? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "usuario",
            null,
            "id_usuario = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario")),
                nombres = cursor.getString(cursor.getColumnIndexOrThrow("nombres")),
                apellidos = cursor.getString(cursor.getColumnIndexOrThrow("apellidos")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                clave = cursor.getString(cursor.getColumnIndexOrThrow("clave")),
                genero = cursor.getString(cursor.getColumnIndexOrThrow("genero"))
            )
        }
        cursor.close()
        return usuario
    }

    /**
     * ACTUALIZAR USUARIO
     */
    fun actualizar(usuario: Usuario): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombres", usuario.nombres)
            put("apellidos", usuario.apellidos)
            put("correo", usuario.correo)
            put("clave", usuario.clave)
            put("genero", usuario.genero)
        }
        return db.update("usuario", valores, "id_usuario = ?", arrayOf(usuario.id.toString()))
    }
}