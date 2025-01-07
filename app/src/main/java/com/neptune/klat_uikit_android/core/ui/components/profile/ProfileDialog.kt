package com.neptune.klat_uikit_android.core.ui.components.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.ui.components.alert.AlertDialog
import com.neptune.klat_uikit_android.core.ui.components.enums.AlertType
import com.neptune.klat_uikit_android.core.ui.interfaces.DialogInterface
import com.neptune.klat_uikit_android.databinding.LayoutProfileDialogBinding
import com.neptune.klat_uikit_android.feature.member.list.MemberInterface

class ProfileDialog(
    private val userId: String,
    private val userNickname: String,
    private val profileImage: String,
    private val memberInterface: MemberInterface
) : DialogFragment(), DialogInterface {
    private var _binding: LayoutProfileDialogBinding? = null
    private val binding get() = _binding ?: error("LayoutProfileDialogBinding 초기화 에러")

    private val isMyProfile: Boolean = userId == ChannelObject.userId
    private val isChannelOwner: Boolean = ChannelObject.userId == ChannelObject.tpChannel.channelOwnerId

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDisplayMetrics()
        setClickListener()
        setUserType()
    }

    private fun setClickListener() = with(binding) {
        ivProfileClose.setOnClickListener { dialog?.dismiss() }
        clProfileBlock.setOnClickListener { showAlertDialog(type = AlertType.BAN) }
        clProfileMute.setOnClickListener { showAlertDialog(type = AlertType.MUTE) }
        clOwner.setOnClickListener { showAlertDialog(type = AlertType.OWNER) }
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
            isMyProfile -> setViewTypeMe()
            isChannelOwner -> setViewTypeOwner()
            !isChannelOwner -> {
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
        ivBadgeIcon.setImageResource(R.drawable.ic_20_add)
        tvBadgeContent.text = "운영자 권한 부여하기"
    }

    private fun setViewTypeChannelOwner() = with(binding) {
        clProfileBlock.visibility = View.GONE
        ivBadgeIcon.setImageResource(R.drawable.ic_20_owner)
        tvBadgeContent.text = "운영자"
    }

    private fun hideOwnerView() = with(binding) {
        clProfileBlock.visibility = View.GONE
        clOwner.visibility = View.GONE
    }

    override fun peerMuteUser() {

    }

    override fun unPeerMuteUser() {

    }

    override fun muteUser() {

    }

    override fun unMuteUser() {

    }

    override fun banUser(banId: String) {
        memberInterface.updateMembers(banId)
        dialog?.dismiss()
    }

    override fun grantOwner() {

    }
}