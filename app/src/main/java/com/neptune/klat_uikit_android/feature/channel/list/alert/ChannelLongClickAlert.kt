package com.neptune.klat_uikit_android.feature.channel.list.alert

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.neptune.klat_uikit_android.databinding.LayoutChannelLongClickAlertBinding

class ChannelLongClickAlert(
    private val channelName: String,
    private val channelLongClickListener: ChannelLongClickListener
) : DialogFragment() {
    private var _binding: LayoutChannelLongClickAlertBinding? = null
    private val binding get() = _binding ?: error("LayoutChannelLongClickAlertBinding 초기화 에러")
    private val adapter: ChannelAlertAdapter by lazy { setAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutChannelLongClickAlertBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setDisplayMetrics()
        init()
    }

    private fun init() = with(binding) {
        tvChannelAlertName.text = channelName
        rvChannelOption.apply {
            adapter = this@ChannelLongClickAlert.adapter
        }
    }

    private fun setDisplayMetrics() {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.78).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun setAdapter(): ChannelAlertAdapter {
        return ChannelAlertAdapter { position ->
            when (position) {
                ChannelAlertAdapter.MARK_AS_READ -> channelLongClickListener.markAsRead()
                ChannelAlertAdapter.LEAVE_CHANNEL -> channelLongClickListener.leaveChannel()
                else -> Unit
            }
            dismiss()
        }
    }
}