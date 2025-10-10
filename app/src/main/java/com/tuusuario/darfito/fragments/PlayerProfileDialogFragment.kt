package com.tuusuario.darfito

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.tuusuario.darfito.model.Player
import java.text.NumberFormat
import java.util.*


class PlayerProfileDialogFragment : DialogFragment() {

    private lateinit var ivProfileAvatar: ImageView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileId: TextView
    private lateinit var tvProfileScore: TextView
    private lateinit var tvProfileLevel: TextView
    private lateinit var btnFollow: MaterialButton
    private lateinit var btnClose: MaterialButton

    private var player: Player? = null
    private var isFollowing = false

    companion object {
        private const val ARG_PLAYER_ID = "player_id"
        private const val ARG_PLAYER_NAME = "player_name"
        private const val ARG_PLAYER_SCORE = "player_score"
        private const val ARG_PLAYER_LEVEL = "player_level"
        private const val ARG_PLAYER_AVATAR = "player_avatar"

        fun newInstance(player: Player): PlayerProfileDialogFragment {
            val fragment = PlayerProfileDialogFragment()
            val args = Bundle().apply {
                putString(ARG_PLAYER_ID, player.id)
                putString(ARG_PLAYER_NAME, player.name)
                putInt(ARG_PLAYER_SCORE, player.score)
                putString(ARG_PLAYER_LEVEL, player.level)
                putInt(ARG_PLAYER_AVATAR, player.avatarResId)
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

        initViews(view)
        loadPlayerData()
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
            player = Player(
                id = args.getString(ARG_PLAYER_ID) ?: "",
                name = args.getString(ARG_PLAYER_NAME) ?: "",
                score = args.getInt(ARG_PLAYER_SCORE),
                level = args.getString(ARG_PLAYER_LEVEL) ?: "",
                avatarResId = args.getInt(ARG_PLAYER_AVATAR)
            )

            player?.let { displayPlayerInfo(it) }
        }
    }

    private fun displayPlayerInfo(player: Player) {
        tvProfileName.text = player.name
        tvProfileId.text = "ID: ${player.id}"

        val formattedScore = NumberFormat.getNumberInstance(Locale.getDefault())
            .format(player.score)
        tvProfileScore.text = formattedScore

        tvProfileLevel.text = player.level
        ivProfileAvatar.setImageResource(R.drawable.ic_person)
    }

    private fun setupClickListeners() {
        btnFollow.setOnClickListener {
            toggleFollow()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun toggleFollow() {
        isFollowing = !isFollowing

        if (isFollowing) {
            btnFollow.text = "SIGUIENDO"
            btnFollow.setIconResource(R.drawable.ic_check)
            Toast.makeText(
                requireContext(),
                "Ahora sigues a ${player?.name}",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            btnFollow.text = "SEGUIR"
            btnFollow.setIconResource(R.drawable.ic_add)
            Toast.makeText(
                requireContext(),
                "Dejaste de seguir a ${player?.name}",
                Toast.LENGTH_SHORT
            ).show()
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