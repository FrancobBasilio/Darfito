package com.tuusuario.darfito.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.tuusuario.darfito.R
import com.tuusuario.darfito.data.dao.AmigoDAO
import com.tuusuario.darfito.data.dao.SolicitudDAO
import com.tuusuario.darfito.data.dao.UsuarioDAO
import com.tuusuario.darfito.model.Player
import java.text.NumberFormat
import java.util.Locale

class PlayerProfileDialogFragment : DialogFragment() {

    private lateinit var ivProfileAvatar: ImageView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileId: TextView
    private lateinit var tvProfileScore: TextView
    private lateinit var tvProfileLevel: TextView
    private lateinit var btnFollow: MaterialButton
    private lateinit var btnClose: MaterialButton

    private var player: Player? = null
    private var estadoAmistad: EstadoAmistad = EstadoAmistad.DESCONOCIDO

    // DAOs
    private lateinit var usuarioDAO: UsuarioDAO
    private lateinit var solicitudDAO: SolicitudDAO
    private lateinit var amigoDAO: AmigoDAO

    private var usuarioActualId: Int = -1

    enum class EstadoAmistad {
        DESCONOCIDO,
        YA_AMIGOS,
        SOLICITUD_ENVIADA,
        PUEDE_ENVIAR
    }

    companion object {
        private const val ARG_PLAYER_ID = "player_id"
        private const val ARG_USUARIO_ID = "usuario_id"
        private const val ARG_PLAYER_SCORE = "player_score"
        private const val ARG_PLAYER_LEVEL = "player_level"
        private const val ARG_PLAYER_AVATAR = "player_avatar"
        private const val ARG_USUARIO_ACTUAL_ID = "usuario_actual_id"

        fun newInstance(player: Player, usuarioActualId: Int): PlayerProfileDialogFragment {
            val fragment = PlayerProfileDialogFragment()
            val args = Bundle().apply {
                putString(ARG_PLAYER_ID, player.id)
                putInt(ARG_USUARIO_ID, player.usuarioId)
                putInt(ARG_PLAYER_SCORE, player.score)
                putString(ARG_PLAYER_LEVEL, player.level)
                putInt(ARG_PLAYER_AVATAR, player.avatarResId)
                putInt(ARG_USUARIO_ACTUAL_ID, usuarioActualId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_player_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar DAOs
        usuarioDAO = UsuarioDAO(requireContext())
        solicitudDAO = SolicitudDAO(requireContext())
        amigoDAO = AmigoDAO(requireContext())

        initViews(view)
        loadPlayerData()
        verificarEstadoAmistad()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        ivProfileAvatar = view.findViewById(R.id.ivProfileAvatar)
        tvProfileName = view.findViewById(R.id.tvProfileName)
        tvProfileId = view.findViewById(R.id.tvProfileId)
        tvProfileScore = view.findViewById(R.id.tvProfileScore)
        tvProfileLevel = view.findViewById(R.id.tvProfileLevel)
        btnFollow = view.findViewById(R.id.btnFollow)
        btnClose = view.findViewById(R.id.btnClose)
    }

    private fun loadPlayerData() {
        arguments?.let { args ->
            usuarioActualId = args.getInt(ARG_USUARIO_ACTUAL_ID, -1)

            player = Player(
                id = args.getString(ARG_PLAYER_ID) ?: "",
                usuarioId = args.getInt(ARG_USUARIO_ID),
                score = args.getInt(ARG_PLAYER_SCORE),
                level = args.getString(ARG_PLAYER_LEVEL) ?: "",
                avatarResId = args.getInt(ARG_PLAYER_AVATAR)
            )

            player?.let { displayPlayerInfo(it) }
        }
    }

    private fun displayPlayerInfo(player: Player) {
        // Obtener nombre del usuario desde la BD
        val usuario = usuarioDAO.obtenerPorId(player.usuarioId)
        tvProfileName.text = usuario?.nombres ?: "Jugador"
        tvProfileId.text = "ID: ${player.id}"

        val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault())
            .format(player.score)
        tvProfileScore.text = formattedScore

        tvProfileLevel.text = player.level
        ivProfileAvatar.setImageResource(player.avatarResId)
    }

    private fun verificarEstadoAmistad() {
        player?.let { p ->
            // No puede seguirse a sí mismo
            if (p.usuarioId == usuarioActualId) {
                btnFollow.visibility = View.GONE
                return
            }

            // Verificar si ya son amigos
            if (amigoDAO.sonAmigos(usuarioActualId, p.usuarioId)) {
                estadoAmistad = EstadoAmistad.YA_AMIGOS
                btnFollow.text = "YA SON AMIGOS"
                btnFollow.setIconResource(R.drawable.ic_check)
                btnFollow.isEnabled = false
                return
            }

            if (solicitudDAO.existeSolicitudPendiente(usuarioActualId, p.usuarioId)) {
                estadoAmistad = EstadoAmistad.SOLICITUD_ENVIADA
                btnFollow.text = "PENDIENTE"
                btnFollow.setIconResource(R.drawable.ic_check)
                btnFollow.isEnabled = false
                return
            }


            // Verificar si ya existe solicitud pendiente
                estadoAmistad = EstadoAmistad.PUEDE_ENVIAR
                btnFollow.text = "SEGUIR"
                btnFollow.setIconResource(R.drawable.ic_add)
                btnFollow.isEnabled = true

        }
    }

    private fun setupClickListeners() {
        btnFollow.setOnClickListener {
            enviarSolicitud()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun enviarSolicitud() {
        player?.let { p ->
            val resultado = solicitudDAO.insertar(usuarioActualId, p.usuarioId)

            if (resultado != -1L) {
                Toast.makeText(
                    requireContext(),
                    "Solicitud enviada correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                btnFollow.text = "SOLICITUD ENVIADA"
                btnFollow.isEnabled = false
                estadoAmistad = EstadoAmistad.SOLICITUD_ENVIADA
            } else {
                Toast.makeText(
                    requireContext(),
                    "No se pudo enviar la solicitud. Puede que ya exista una pendiente.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}