package com.tuusuario.darfito

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.tuusuario.darfito.model.Usuario
import com.tuusuario.darfito.repo.UsuariosRepository
import java.util.regex.Pattern

class RegistroActivity : AppCompatActivity() {

    private lateinit var tietNombre: TextInputEditText
    private lateinit var tietCorreo: TextInputEditText
    private lateinit var tietPassword: TextInputEditText
    private lateinit var tietConfirmPassword: TextInputEditText
    private lateinit var tilNombre: TextInputLayout
    private lateinit var tilCorreo: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var chipGroupSexo: ChipGroup
    private lateinit var chipMasculino: Chip
    private lateinit var chipFemenino: Chip
    private lateinit var chipOtro: Chip
    private lateinit var cbTerminos: MaterialCheckBox
    private lateinit var btnGuardar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)

        inicializarVistas()
        configurarListeners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun inicializarVistas() {
        tietNombre = findViewById(R.id.etNombre)
        tietCorreo = findViewById(R.id.etCorreo)
        tietPassword = findViewById(R.id.etPassword)
        tietConfirmPassword = findViewById(R.id.etConfirmPassword)
        tilNombre = findViewById(R.id.tilNombre)
        tilCorreo = findViewById(R.id.tilCorreo)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        chipGroupSexo = findViewById(R.id.chipGroupSexo)
        chipMasculino = findViewById(R.id.chipMasculino)
        chipFemenino = findViewById(R.id.chipFemenino)
        chipOtro = findViewById(R.id.chipOtro)
        cbTerminos = findViewById(R.id.cbTerminos)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun configurarListeners() {
        btnGuardar.setOnClickListener {
            validarCampos()
        }

        tietNombre.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilNombre.error = null
        }
        tietCorreo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilCorreo.error = null
        }
        tietPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilPassword.error = null
        }
        tietConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) tilConfirmPassword.error = null
        }
    }

    private fun validarCampos() {
        val nombre = tietNombre.text.toString().trim()
        val correo = tietCorreo.text.toString().trim()
        val password = tietPassword.text.toString().trim()
        val confirmPassword = tietConfirmPassword.text.toString().trim()
        var error = false


        if (nombre.isEmpty()) {
            tilNombre.error = "Ingrese su nombre completo"
            error = true
        } else if (nombre.length < 3) {
            tilNombre.error = "El nombre debe tener al menos 3 caracteres"
            error = true
        } else {
            tilNombre.error = null
        }


        if (correo.isEmpty()) {
            tilCorreo.error = "Ingrese un correo electrónico"
            error = true
        } else if (!validarFormatoCorreo(correo)) {
            tilCorreo.error = "Correo electrónico inválido"
            error = true
        } else {
            tilCorreo.error = null
        }


        if (password.isEmpty()) {
            tilPassword.error = "Ingrese una contraseña"
            error = true
        } else if (password.length < 4) {
            tilPassword.error = "La contraseña debe tener al menos 4 caracteres"
            error = true
        } else {
            tilPassword.error = null
        }


        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Confirme su contraseña"
            error = true
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = "Las contraseñas no coinciden"
            error = true
        } else {
            tilConfirmPassword.error = null
        }

        if (chipGroupSexo.checkedChipId == -1) {
            Toast.makeText(this, "Seleccione un género", Toast.LENGTH_SHORT).show()
            error = true
        }


        if (!cbTerminos.isChecked) {
            Toast.makeText(this, "Debe aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
            error = true
        }

        if (error) {
            return
        } else {
            registrarUsuario(nombre, correo, password)
        }
    }

    private fun validarFormatoCorreo(correo: String): Boolean {
        val patron = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return patron.matcher(correo).matches()
    }

    private fun obtenerGeneroSeleccionado(): String {
        return when (chipGroupSexo.checkedChipId) {
            R.id.chipMasculino -> "Masculino"
            R.id.chipFemenino -> "Femenino"
            R.id.chipOtro -> "Otro"
            else -> ""
        }
    }

    private fun registrarUsuario(nombre: String, correo: String, password: String) {
        val genero = obtenerGeneroSeleccionado()
        val partesNombre = nombre.split(" ", limit = 2)
        val nombres = partesNombre.getOrNull(0) ?: nombre
        val apellidos = partesNombre.getOrNull(1) ?: ""


        val nuevoId = UsuariosRepository.obtenerSiguienteId()
        val nuevoUsuario = Usuario(
            codigo = nuevoId,
            nombres = nombres,
            apellidos = apellidos,
            correo = correo,
            clave = password,
            genero = genero
        )


        UsuariosRepository.agregarUsuario(nuevoUsuario)

        Toast.makeText(
            this,
            "Cuenta creada exitosamente. Bienvenido $nombres",
            Toast.LENGTH_LONG
        ).show()


        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}