package com.tuusuario.darfito

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AccesoActivity : AppCompatActivity() {
    var tvRegistro: TextView?=null
    private lateinit var tietCorreo : TextInputEditText
    private lateinit var tietClave : TextInputEditText
    private lateinit var tilCorreo : TextInputLayout
    private lateinit var tilClave : TextInputLayout
    private lateinit var btnAcceso : Button

    //Lista simulada de usuarios (por ahora solo memoria)
    private val listaUsuarios = mutableListOf(
        Usuario(1, "Bryant Alejandro", "Yacila Valenzuela", "pbyacila@cibertec.edu.pe", "0000"),
        Usuario(2, "Nombres", "Apellidos", "prueba@cibertec.edu.pe", "1234")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_acceso)

        tvRegistro = findViewById(R.id.tvRegistro)
        tietCorreo = findViewById(R.id.tietCorreo)
        tietClave = findViewById(R.id.tietClave)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilClave = findViewById(R.id.tilClave)
        btnAcceso = findViewById(R.id.btnInicio)

        btnAcceso.setOnClickListener {
            validarCampos()
        }

        tvRegistro?.setOnClickListener {
            cambioActivity(RegistroActivity::class.java)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun validarCampos() {
        val correo = tietCorreo.text.toString().trim()
        val clave = tietClave.text.toString().trim()
        var error : Boolean = false
        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo"
            error = true
        } else {
            tilCorreo.error = ""
        }
        if (clave.isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            error = true
        } else {
            tilClave.error = ""
        }

        if (error) {
            return
        } else {
            //Si pasa las validaciones
            Toast.makeText(this, "Validación correcta. Procesando login...", Toast.LENGTH_LONG).show()

            var usuarioEncontrado : Usuario ?= null
//            for (i in 0 until listaUsuarios.size) {
//                if (listaUsuarios[i].correo == correo + "@cibertec.edu.pe" && listaUsuarios[i].clave == clave) {
//                    usuarioEncontrado = listaUsuarios[i]
//                    break
//                }
//            }
            for (u in listaUsuarios) {
                if (u.correo == correo + "@cibertec.edu.pe" && u.clave == clave) {
                    usuarioEncontrado = u
                    break
                }
            }

            if (usuarioEncontrado != null) {
                Toast.makeText(this, "Bienvenido ${usuarioEncontrado.nombres}", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun cambioActivity(activityDestino : Class<out Activity>) {
        val intent = Intent(this, activityDestino)
        startActivity(intent)
    }

    fun abrirVentanaNavegador() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData("http://www.google.com".toUri())
        startActivity(intent)
    }

    fun abrirBuscadorWeb() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.setData("http://www.google.com".toUri())
        startActivity(intent)
    }

    fun abrirLlamada() {
        val intent = Intent(Intent.ACTION_DIAL)
        startActivity(intent)
    }

    fun llamar() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.setData("tel:+51999999999".toUri())
        startActivity(intent)
    }
}