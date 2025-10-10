package com.tuusuario.darfito.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tuusuario.darfito.R
import com.tuusuario.darfito.model.Player
import com.tuusuario.darfito.repo.PlayerRepositorio
import java.text.NumberFormat
import java.util.*

class PerfilFragment : Fragment() {

    private lateinit var ivProfileAvatar: ImageView
    private lateinit var fabEditAvatar: FloatingActionButton
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileId: TextView
    private lateinit var tvProfileScore: TextView
    private lateinit var tvProfileLevel: TextView

    private var currentPlayer: Player? = null
    private var selectedAvatarResId: Int = R.drawable.ic_person

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
        // Aquí deberías obtener el jugador actual logueado
        // Por ahora usamos el primero de la lista como ejemplo
        currentPlayer = PlayerRepositorio.obtenerPlayer().firstOrNull()

        currentPlayer?.let { player ->
            displayPlayerInfo(player)
        } ?: run {
            Toast.makeText(
                requireContext(),
                "No se encontró información del jugador",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun displayPlayerInfo(player: Player) {
        tvProfileName.text = player.name
        tvProfileId.text = player.id

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

            // Aquí podrías guardar la URI en SharedPreferences o base de datos
            // saveImageUri(uri)
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

        // Actualizar en el repositorio
        currentPlayer?.let { player ->
            val updatedPlayer = player.copy(avatarResId = avatarResId)
            val success = PlayerRepositorio.actualizarPlayer(updatedPlayer)

            if (success) {
                currentPlayer = updatedPlayer
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