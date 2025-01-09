package com.neptune.klat_uikit_android.core.ui.components.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.ui.components.alert.AlertDialog
import com.neptune.klat_uikit_android.core.ui.components.enums.AlertType
import com.neptune.klat_uikit_android.core.ui.interfaces.DialogInterface
import com.neptune.klat_uikit_android.databinding.LayoutProfileDialogBinding
import com.neptune.klat_uikit_android.feature.member.list.MemberInterface
import kotlinx.coroutines.launch

class ProfileDialog(
    private val userId: String,
    private val userNickname: String,
    private val profileImage: String,
    private val memberInterface: MemberInterface
) : DialogFragment(), DialogInterface {
    private var _binding: LayoutProfileDialogBinding? = null
    private val binding get() = _binding ?: error("LayoutProfileDialogBinding 초기화 에러")
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setDisplayMetrics()
        observeProfileUiState()
    }

    private fun init() {
        viewModel.setUserId(userId)
        when (viewModel.isChannelOwner) {
            true -> setView()
            false -> viewModel.getPeerMutedUsers()
        }
    }

    private fun observeProfileUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.profileUiState.collect { profileUiState ->
                    handleProfileUiState(profileUiState)
                }
            }
        }
    }

    private fun handleProfileUiState(profileUiState: ProfileUiState) {
        when (profileUiState) {
            is ProfileUiState.BaseState -> { }
            is ProfileUiState.GetPeerMutedUsers -> setView()
            is ProfileUiState.UnMuteUser -> unMutedUI()
            is ProfileUiState.PeerUnMuteUser -> unMutedUI()
        }
    }

    private fun setClickListener() = with(binding) {
        ivProfileClose.setOnClickListener { dialog?.dismiss() }
        clProfileBlock.setOnClickListener { showAlertDialog(type = AlertType.BAN) }
        clOwner.setOnClickListener { showAlertDialog(type = AlertType.OWNER) }
        clProfileMute.setOnClickListener {
            when {
                viewModel.isMuted && viewModel.isChannelOwner -> viewModel.unMuteUser(userId)
                viewModel.isMuted && !viewModel.isChannelOwner -> viewModel.peerUnMuteUser(userId)
                else -> showAlertDialog(type = AlertType.MUTE)
            }
        }
    }

    private fun setDisplayMetrics() {
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.drawable.bg_radius_8dp)
            setLayout(
                (resources.displayMetrics.widthPixels * 0.88).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun setUserType() = with(binding) {
        tvProfileUserNickname.text = userNickname
        ivProfileThumbnail.loadThumbnail(profileImage)

        when {
            viewModel.isMyProfile -> setViewTypeMe()
            viewModel.isChannelOwner -> setViewTypeOwner()
            !viewModel.isChannelOwner -> {
                when (userId == ChannelObject.tpChannel.channelOwnerId) {
                    true -> setViewTypeChannelOwner()
                    false -> hideOwnerView()
                }
            }
        }
    }

    private fun showAlertDialog(type: AlertType) {
        AlertDialog(
            alertType = type,
            userId = userId,
            userNickname = userNickname,
            dialogInterface = this
        ).showNow(parentFragmentManager, null)
    }

    private fun setViewTypeMe() = with(binding) {
        clOwner.visibility = View.GONE
        clBlock.visibility = View.GONE
        tvProfileUserNickname.text = "$userNickname(나)"
    }

    private fun setViewTypeOwner() = with(binding) {
        checkMutedUser()
        ivBadgeIcon.setImageResource(R.drawable.ic_20_add)
        tvBadgeContent.text = "운영자 권한 부여하기"
    }

    private fun setViewTypeChannelOwner() = with(binding) {
        checkPeerMutedUser()
        clProfileBlock.visibility = View.GONE
        ivBadgeIcon.setImageResource(R.drawable.ic_20_owner)
        tvBadgeContent.text = "운영자"
    }

    private fun hideOwnerView() = with(binding) {
        checkPeerMutedUser()
        clProfileBlock.visibility = View.GONE
        clOwner.visibility = View.GONE
    }

    private fun setView() {
        setClickListener()
        setUserType()
    }

    private fun checkPeerMutedUser() {
        viewModel.peerMutedUsers.forEach { tpMember ->
            if (tpMember.userId == userId) {
                viewModel.setMute(true)
                return@forEach
            }
        }

        when (viewModel.isMuted) {
            true -> mutedUI()
            false -> unMutedUI()
        }
    }

    private fun checkMutedUser() {
        ChannelObject.tpChannel.mutedUsers.forEach { tpMember ->
            if (tpMember.userId == userId) {
                viewModel.setMute(true)
                return@forEach
            }
        }

        when (viewModel.isMuted) {
            true -> mutedUI()
            false -> unMutedUI()
        }
    }

    private fun unMutedUI() {
        binding.ivProfileMute.setImageResource(R.drawable.ic_64dp_bell_off)
        binding.tvProfileMute.text = "음소거"
    }

    private fun mutedUI() {
        binding.ivProfileMute.setImageResource(R.drawable.ic_64dp_bell_on)
        binding.tvProfileMute.text = "음소거 됨"
    }

    override fun peerMuteUser() {
        mutedUI()
    }

    override fun muteUser() {
        mutedUI()
    }

    override fun banUser(banId: String) {
        memberInterface.updateMembers(banId)
        dialog?.dismiss()
    }

    override fun grantOwner(ownerId: String) {
        setViewTypeChannelOwner()
        memberInterface.updateOwner(ownerId)
    }
}