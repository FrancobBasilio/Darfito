package com.tuusuario.darfito.repo

import android.os.Build
import androidx.annotation.RequiresApi
import com.tuusuario.darfito.R
import com.tuusuario.darfito.model.Player
import com.tuusuario.darfito.model.Usuario

object PlayerRepositorio{
    private val listaPlayer = mutableListOf(
        Player("D123453", "Monico", 123454, "Nivel Intermedio", R.drawable.popeye),
        Player("D123553", "Leonin", 133454, "Nivel Experto", R.drawable.goku)
    )

    fun obtenerPlayer(): List<Player> = listaPlayer

    fun agregarPlayer(player: Player) {
        listaPlayer.add(player)
    }

    fun obtenerSiguienteId(): Int {
        return (if (listaPlayer.isEmpty()) 1 else listaPlayer.maxOf { it.id } + 1) as Int
    }

    fun buscarPlayer(name: String ): Player? {
        return listaPlayer.find { it.name == name }
    }

    fun actualizarPlayer(playerActualizado: Player): Boolean {
        val index = listaPlayer.indexOfFirst { it.id == playerActualizado.id }
        return if (index != -1) {
            listaPlayer[index] = playerActualizado
            true
        } else {
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun eliminarPlayer(id: String): Boolean {
        return listaPlayer.removeIf { it.id == id }
    }

}