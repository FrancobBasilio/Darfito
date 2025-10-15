package com.tuusuario.darfito.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tuusuario.darfito.HomeActivity
import com.tuusuario.darfito.R
import com.tuusuario.darfito.data.dao.PlayerDAO
import com.tuusuario.darfito.data.dao.UsuarioDAO
import com.tuusuario.darfito.model.Player
import java.text.NumberFormat
import java.util.Locale

class PerfilFragment : Fragment() {

    private lateinit var ivProfileAvatar: ImageView
    private lateinit var fabEditAvatar: FloatingActionButton
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileId: TextView
    private lateinit var tvProfileScore: TextView
    private lateinit var tvProfileLevel: TextView

    private var currentPlayer: Player? = null
    private var selectedAvatarResId: Int = R.drawable.ic_person
    private var usuarioId: Int = -1

    // DAOs
    private lateinit var playerDAO: PlayerDAO
    private lateinit var usuarioDAO: UsuarioDAO

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleImageSelection(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar DAOs
        playerDAO = PlayerDAO(requireContext())
        usuarioDAO = UsuarioDAO(requireContext())

        // Obtener usuario ID
        usuarioId = (activity as? HomeActivity)?.obtenerUsuarioId() ?: -1

        initViews(view)
        loadCurrentPlayer()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar)
        fabEditAvatar = view.findViewById(R.id.fabEditAvatar)
        tvProfileName = view.findViewById(R.id.tvProfileName)
        tvProfileId = view.findViewById(R.id.tvProfileId)
        tvProfileScore = view.findViewById(R.id.tvProfileScore)
        tvProfileLevel = view.findViewById(R.id.tvProfileLevel)
    }

    private fun loadCurrentPlayer() {
        if (usuarioId == -1) {
            Toast.makeText(
                requireContext(),
                "Error: Usuario no identificado",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Buscar player del usuario actual desde la BD
        currentPlayer = playerDAO.buscarPorUsuarioId(usuarioId)

        currentPlayer?.let { player ->
            displayPlayerInfo(player)
        } ?: run {
            // Si no existe player, crear uno por defecto
            crearPlayerPorDefecto()
        }
    }

    private fun crearPlayerPorDefecto() {
        val nuevoPlayer = Player(
            id = "0",
            usuarioId = usuarioId,
            score = 0,
            level = "Nivel Básico",
            avatarResId = R.drawable.ic_person
        )

        val resultado = playerDAO.insertar(nuevoPlayer)

        if (resultado != -1L) {
            // Recargar el player recién creado
            currentPlayer = playerDAO.buscarPorUsuarioId(usuarioId)
            currentPlayer?.let { displayPlayerInfo(it) }
        } else {
            Toast.makeText(
                requireContext(),
                "Error al crear perfil de jugador",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayPlayerInfo(player: Player) {
        // Obtener nombre del usuario
        val usuario = usuarioDAO.obtenerPorId(player.usuarioId)
        tvProfileName.text = usuario?.nombres ?: "Jugador"
        tvProfileId.text = "ID: ${player.id}"

        // Formatear el score con separadores de miles
        val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault())
            .format(player.score)
        tvProfileScore.text = formattedScore

        tvProfileLevel.text = player.level

        // Cargar avatar
        selectedAvatarResId = player.avatarResId
        ivProfileAvatar.setImageResource(player.avatarResId)
    }

    private fun setupClickListeners() {
        fabEditAvatar.setOnClickListener {
            showAvatarSelectionDialog()
        }
    }

    private fun showAvatarSelectionDialog() {
        val dialog = AvatarSelectionDialogFragment { selectedAvatar ->
            updatePlayerAvatar(selectedAvatar)
        }
        dialog.show(parentFragmentManager, "AvatarSelectionDialog")
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun handleImageSelection(uri: Uri) {
        try {
            ivProfileAvatar.setImageURI(uri)
            Toast.makeText(
                requireContext(),
                "Foto actualizada exitosamente",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Error al cargar la imagen",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updatePlayerAvatar(avatarResId: Int) {
        selectedAvatarResId = avatarResId
        ivProfileAvatar.setImageResource(avatarResId)

        currentPlayer?.let { player ->
            val updatedPlayer = player.copy(avatarResId = avatarResId)
            val rowsAffected = playerDAO.actualizar(updatedPlayer)

            if (rowsAffected > 0) {
                currentPlayer = updatedPlayer

                // ✅ ACTUALIZAR EL HEADER DEL MENU
                (activity as? HomeActivity)?.actualizarHeader()

                Toast.makeText(
                    requireContext(),
                    "Avatar actualizado exitosamente",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al actualizar el avatar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        // Recargar datos al volver al fragment
        loadCurrentPlayer()
    }
}