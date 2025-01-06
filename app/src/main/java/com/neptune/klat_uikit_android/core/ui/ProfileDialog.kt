package com.neptune.klat_uikit_android.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.LayoutProfileDialogBinding

class ProfileDialog(
    private val userId: String,
    private val userNickname: String,
    private val profileImage: String
) : DialogFragment() {
    private var _binding: LayoutProfileDialogBinding? = null
    private val binding get() = _binding ?: error("LayoutProfileDialogBinding 초기화 에러")
    private val isOwner: Boolean = ChannelObject.userId == ChannelObject.tpChannel.channelOwnerId

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDisplayMetrics()
        setClickListener()
        bindView()
    }

    private fun bindView() = with(binding) {
        tvProfileUserNickname.text = userNickname
        ivProfileThumbnail.loadThumbnail(profileImage)

        when {
            userId == ChannelObject.userId -> {
                clOwner.visibility = View.GONE
                clBlock.visibility = View.GONE
                tvProfileUserNickname.text = "$userNickname(나)"
            }

            isOwner -> {
                ivBadgeIcon.setImageResource(R.drawable.ic_20_add)
                tvBadgeContent.text = "운영자 권한 부여하기"
            }

            !isOwner -> {
                when (userId == ChannelObject.tpChannel.channelOwnerId) {
                    true -> {
                        clProfileBlock.visibility = View.GONE
                        ivBadgeIcon.setImageResource(R.drawable.ic_20_owner)
                        tvBadgeContent.text = "운영자"
                    }

                    false -> {
                        clProfileBlock.visibility = View.GONE
                        clOwner.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setClickListener() {
        binding.clProfileBlock.setOnClickListener { AlertDialog().showNow(parentFragmentManager, null) }
        binding.clProfileMute.setOnClickListener { AlertDialog().showNow(parentFragmentManager, null) }
    }

    private fun setDisplayMetrics() {
        dialog?.window?.apply {
            setBackgroundDrawableResource(R.drawable.bg_radius_8dp) // 배경 설정
            setLayout(
                (resources.displayMetrics.widthPixels * 0.88).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}