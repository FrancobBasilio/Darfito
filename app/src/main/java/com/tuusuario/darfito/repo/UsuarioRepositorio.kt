package com.tuusuario.darfito.repo

import com.tuusuario.darfito.model.Usuario

object UsuariosRepository {
    private val listaUsuarios = mutableListOf(
        Usuario(1, "Bryant Alejandro", "Yacila Valenzuela", "pbyacila@cibertec.edu.pe", "0000"),
        Usuario(2, "Nombres", "Apellidos", "prueba@cibertec.edu.pe", "1234")
    )

    fun obtenerUsuarios(): List<Usuario> = listaUsuarios

    fun agregarUsuario(usuario: Usuario) {
        listaUsuarios.add(usuario)
    }

    fun obtenerSiguienteId(): Int {
        return if (listaUsuarios.isEmpty()) 1 else listaUsuarios.maxOf { it.codigo } + 1
    }

    fun buscarUsuario(correo: String, clave: String): Usuario? {
        return listaUsuarios.find { it.correo == correo && it.clave == clave }
    }
}