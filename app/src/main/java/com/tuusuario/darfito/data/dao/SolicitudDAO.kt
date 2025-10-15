package com.tuusuario.darfito.data.dao

import android.content.ContentValues
import android.content.Context
import com.tuusuario.darfito.data.AppDatabaseHelper
import com.tuusuario.darfito.model.Solicitud
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SolicitudDAO(context: Context) {

    private val dbHelper = AppDatabaseHelper(context)

    /**
     * INSERTAR NUEVA SOLICITUD
     */
    fun insertar(usuarioEnviaId: Int, usuarioRecibeId: Int): Long {
        // Verificar que no se envíe solicitud a sí mismo
        if (usuarioEnviaId == usuarioRecibeId) {
            return -1L
        }

        // Verificar que no exista solicitud pendiente
        if (existeSolicitudPendiente(usuarioEnviaId, usuarioRecibeId)) {
            return -1L
        }

        // Verificar que no sean amigos ya
        if (sonAmigos(usuarioEnviaId, usuarioRecibeId)) {
            return -1L
        }

        val db = dbHelper.writableDatabase
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val valores = ContentValues().apply {
            put("id_usuario_envia", usuarioEnviaId)
            put("id_usuario_recibe", usuarioRecibeId)
            put("estado", "pendiente")
            put("fecha_solicitud", fechaActual)
        }

        return db.insert("solicitud", null, valores)
    }

    /**
     * OBTENER SOLICITUDES PENDIENTES DE UN USUARIO
     */
    fun obtenerSolicitudesPendientes(usuarioId: Int): List<Solicitud> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "solicitud",
            null,
            "id_usuario_recibe = ? AND estado = ?",
            arrayOf(usuarioId.toString(), "pendiente"),
            null, null, "fecha_solicitud DESC"
        )

        val solicitudes = mutableListOf<Solicitud>()

        while (cursor.moveToNext()) {
            solicitudes.add(
                Solicitud(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id_solicitud")),
                    usuarioEnviaId = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario_envia")),
                    usuarioRecibeId = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario_recibe")),
                    estado = cursor.getString(cursor.getColumnIndexOrThrow("estado")),
                    fechaSolicitud = cursor.getString(cursor.getColumnIndexOrThrow("fecha_solicitud"))
                )
            )
        }
        cursor.close()
        return solicitudes
    }

    /**
     * ACEPTAR SOLICITUD
     */
    fun aceptarSolicitud(solicitudId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("estado", "aceptada")
        }

        val rowsAffected = db.update(
            "solicitud",
            valores,
            "id_solicitud = ?",
            arrayOf(solicitudId.toString())
        )

        return rowsAffected > 0
    }

    /**
     * RECHAZAR SOLICITUD
     */
    fun rechazarSolicitud(solicitudId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("estado", "rechazada")
        }

        val rowsAffected = db.update(
            "solicitud",
            valores,
            "id_solicitud = ?",
            arrayOf(solicitudId.toString())
        )

        return rowsAffected > 0
    }

    /**
     * ELIMINAR SOLICITUD
     */
    fun eliminar(solicitudId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("solicitud", "id_solicitud = ?", arrayOf(solicitudId.toString()))
    }

    /**
     * VERIFICAR SI EXISTE SOLICITUD PENDIENTE
     */
    private fun existeSolicitudPendiente(usuarioEnviaId: Int, usuarioRecibeId: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "solicitud",
            arrayOf("id_solicitud"),
            "(id_usuario_envia = ? AND id_usuario_recibe = ? AND estado = ?) OR " +
                    "(id_usuario_envia = ? AND id_usuario_recibe = ? AND estado = ?)",
            arrayOf(
                usuarioEnviaId.toString(), usuarioRecibeId.toString(), "pendiente",
                usuarioRecibeId.toString(), usuarioEnviaId.toString(), "pendiente"
            ),
            null, null, null
        )

        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    /**
     * VERIFICAR SI SON AMIGOS (necesita AmigoDAO)
     */
    private fun sonAmigos(usuario1Id: Int, usuario2Id: Int): Boolean {
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
}