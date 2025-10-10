package com.tuusuario.darfito.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tuusuario.darfito.R
import com.tuusuario.darfito.adapter.AvatarAdapter

class AvatarSelectionDialogFragment(
    private val onAvatarSelected: (Int) -> Unit
) : DialogFragment() {

    private lateinit var rvAvatars: RecyclerView
    private lateinit var btnCancel: MaterialButton
    private lateinit var avatarAdapter: AvatarAdapter

    private var selectedAvatarResId: Int? = null

    companion object {
        val AVAILABLE_AVATARS = listOf(
            R.drawable.popeye,
            R.drawable.goku,
            R.drawable.ic_person,
        )
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
        return inflater.inflate(R.layout.dialog_avatar_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupClickListeners()
    }

    private fun initViews(view: View) {
        rvAvatars = view.findViewById(R.id.rvAvatars)
        btnCancel = view.findViewById(R.id.btnCancel)
    }

    private fun setupRecyclerView() {
        avatarAdapter = AvatarAdapter(AVAILABLE_AVATARS) { avatarResId ->
            selectedAvatarResId = avatarResId
            onAvatarSelected(avatarResId)
            dismiss()
        }

        rvAvatars.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = avatarAdapter
        }
    }

    private fun setupClickListeners() {
        btnCancel.setOnClickListener {
            dismiss()
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