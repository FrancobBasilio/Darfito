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
import com.tuusuario.darfito.repo.UsuariosRepository

class LoginActivity : AppCompatActivity() {

    private var tvRegistro: TextView? = null
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietClave: TextInputEditText
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilClave: TextInputLayout
    private lateinit var btnAcceso: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        inicializarVistas()
        configurarListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inicializarVistas() {
        tvRegistro = findViewById(R.id.tvRegistro)
        tietCorreo = findViewById(R.id.tietCorreo)
        tietClave = findViewById(R.id.tietClave)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilClave = findViewById(R.id.tilClave)
        btnAcceso = findViewById(R.id.btnInicio)
    }

    private fun configurarListeners() {
        btnAcceso.setOnClickListener {
            validarCampos()
        }

        tvRegistro?.setOnClickListener {
            cambioActivity(RegistroActivity::class.java)
        }

        tietCorreo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilCorreo.error = null
        }
        tietClave.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilClave.error = null
        }
    }

    private fun validarCampos() {
        val correo = tietCorreo.text.toString().trim()
        val clave = tietClave.text.toString().trim()
        var error = false

        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo"
            error = true
        } else {
            tilCorreo.error = null
        }

        if (clave.isEmpty()) {
            tilClave.error = "Ingrese contraseña"
            error = true
        } else {
            tilClave.error = null
        }

        if (error) {
            return
        } else {
            iniciarSesion(correo, clave)
        }
    }

    private fun iniciarSesion(correo: String, clave: String) {
        Toast.makeText(this, "Validando datos...", Toast.LENGTH_SHORT).show()

        val usuarioEncontrado = UsuariosRepository.buscarUsuario(correo, clave)

        if (usuarioEncontrado != null) {
                Toast.makeText(
                    this,
                    "Bienvenido ${usuarioEncontrado.nombres}",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("usuario_id", usuarioEncontrado.codigo)
                    putExtra("usuario_nombre", usuarioEncontrado.nombres)

            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(
                this,
                "Usuario o contraseña incorrectos",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun cambioActivity(activityDestino: Class<out Activity>) {
        val intent = Intent(this, activityDestino)
        startActivity(intent)
    }

    private fun abrirVentanaNavegador() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = "http://www.google.com".toUri()
        startActivity(intent)
    }

    private fun abrirBuscadorWeb() {
        val intent = Intent(Intent.ACTION_WEB_SEARCH)
        intent.data = "http://www.google.com".toUri()
        startActivity(intent)
    }

    private fun abrirLlamada() {
        val intent = Intent(Intent.ACTION_DIAL)
        startActivity(intent)
    }

    private fun llamar() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = "tel:+51999999999".toUri()
        startActivity(intent)
    }
}