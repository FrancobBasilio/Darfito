package com.tuusuario.darfito

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.tuusuario.darfito.R
import com.tuusuario.darfito.data.dao.PlayerDAO
import com.tuusuario.darfito.data.dao.UsuarioDAO
import java.text.NumberFormat
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var nv_side: NavigationView
    private lateinit var btnMenu: ImageButton
    private lateinit var tvBienvenida: TextView

    private var usuarioId: Int = -1
    private var usuarioNombre: String = "Usuario"

    // DAOs
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var playerDAO: PlayerDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inicializar DAOs
        usuarioDAO = UsuarioDAO(this)
        playerDAO = PlayerDAO(this)

        // Obtener datos del usuario
        usuarioId = intent.getIntExtra("usuario_id", -1)
        usuarioNombre = intent.getStringExtra("usuario_nombre") ?: ""

        if (usuarioNombre.isEmpty() && usuarioId != -1) {
            val usuario = usuarioDAO.obtenerPorId(usuarioId)
            usuarioNombre = usuario?.nombres ?: "Usuario"
        }

        initViews()
        setupBienvenida()
        setupNavigation()
        setupNavigationHeader()
        setupClickListeners()
        setupBackPressed()
    }

    private fun initViews() {
        tvBienvenida = findViewById(R.id.tvBienvenida)
        drawerLayout = findViewById(R.id.drawerLayout)
        nv_side = findViewById(R.id.nv_side)
        btnMenu = findViewById(R.id.btnMenu)
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        nv_side.setupWithNavController(navController)

        nv_side.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.snv_log_out -> {
                    drawerLayout.closeDrawer(GravityCompat.END)
                    mostrarDialogoCerrarSesion()
                    true
                }
                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(item, navController)
                    if (handled) {
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }
                    handled
                }
            }
        }
    }

    /**
     *  Configurar el header del NavigationView con datos del usuario
     */
    private fun setupNavigationHeader() {
        val headerView = nv_side.getHeaderView(0)

        val ivHeaderAvatar = headerView.findViewById<ImageView>(R.id.ivHeaderAvatar)
        val tvHeaderName = headerView.findViewById<TextView>(R.id.tvHeaderName)
        val tvHeaderScore = headerView.findViewById<TextView>(R.id.tvHeaderScore)
        val tvHeaderLevel = headerView.findViewById<TextView>(R.id.tvHeaderLevel)

        // Cargar nombre
        tvHeaderName.text = usuarioNombre

        // Buscar player del usuario
        val player = playerDAO.buscarPorUsuarioId(usuarioId)

        if (player != null) {
            // Cargar avatar
            ivHeaderAvatar.setImageResource(player.avatarResId)

            // Formatear y mostrar score
            val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault())
                .format(player.score)
            tvHeaderScore.text = formattedScore

            // Mostrar nivel
            tvHeaderLevel.text = player.level
        } else {
            // Valores por defecto si no hay player
            ivHeaderAvatar.setImageResource(R.drawable.ic_person)
            tvHeaderScore.text = "0"
            tvHeaderLevel.text = "Nivel Básico"
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    finish()
                }
            }
        })
    }

    private fun setupClickListeners() {
        btnMenu.setOnClickListener {
            // Actualizar header antes de abrir el menú
            setupNavigationHeader()
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    private fun setupBienvenida() {
        tvBienvenida.text = "Bienvenido a Darfito, $usuarioNombre"
    }

    private fun mostrarDialogoCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { dialog, _ ->
                dialog.dismiss()
                cerrarSesion()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun cerrarSesion() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().apply {
            clear()
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun obtenerUsuarioId(): Int {
        return usuarioId
    }


    fun actualizarHeader() {
        setupNavigationHeader()
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)

        val nuevoUsuarioId = intent.getIntExtra("usuario_id", -1)
        if (nuevoUsuarioId != -1) {
            usuarioId = nuevoUsuarioId

            val nuevoNombre = intent.getStringExtra("usuario_nombre")
            if (!nuevoNombre.isNullOrEmpty()) {
                usuarioNombre = nuevoNombre
            } else {
                val usuario = usuarioDAO.obtenerPorId(usuarioId)
                usuarioNombre = usuario?.nombres ?: "Usuario"
            }

            setupBienvenida()
            setupNavigationHeader()
        }
    }
}