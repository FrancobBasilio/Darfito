package com.tuusuario.darfito.model

data class Usuario (
    var id : Int,
    var nombres : String = "",
    var apellidos : String = "",
    var correo : String = "",
    var clave : String = "",
    val genero: String = "")