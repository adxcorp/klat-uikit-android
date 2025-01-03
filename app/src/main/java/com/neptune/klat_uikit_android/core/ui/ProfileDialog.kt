package com.neptune.klat_uikit_android.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.LayoutProfileDialogBinding
import io.talkplus.entity.channel.TPMessage

class ProfileDialog(
    private val tpMessage: TPMessage,
    private val userId: String,
    private val ownerId: String
) : DialogFragment() {
    private var _binding: LayoutProfileDialogBinding? = null
    private val binding get() = _binding ?: error("LayoutProfileDialogBinding 초기화 에러")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutWidth()
        binding.tvProfileUserNickname.text = tpMessage.username
        binding.ivProfileThumbnail.loadThumbnail(tpMessage.userProfileImage)

        when {
            userId == ownerId -> {
                binding.ivBadgeIcon.setImageResource(R.drawable.ic_20_add)
                binding.tvBadgeContent.text = "운영자 권한 부여하기"
            }

            tpMessage.userId == ownerId -> {
                binding.clProfileBlock.visibility = View.GONE
                binding.ivBadgeIcon.setImageResource(R.drawable.ic_20_owner)
                binding.tvBadgeContent.text = "운영자"
            }

            tpMessage.userId != ownerId -> {
                binding.clProfileBlock.visibility = View.GONE
                binding.clOwner.visibility = View.GONE
            }
        }

        binding.clProfileBlock.setOnClickListener {
            AlertDialog().showNow(parentFragmentManager, null)
        }

        binding.clProfileMute.setOnClickListener {
            AlertDialog().showNow(parentFragmentManager, null)
        }
    }

    private fun setLayoutWidth() {
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 1),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}