package com.neptune.klat_uikit_android.feature.chat

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.extension.getSerializable
import com.neptune.klat_uikit_android.databinding.ActivityChatBinding
import io.talkplus.entity.channel.TPChannel

class ChatActivity : AppCompatActivity() {
    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        intent.getSerializable<TPChannel>(EXTRA_TP_CHANNEL)?.let { tpChannel ->
            viewModel.setTPChannel(tpChannel)
            viewModel.getMessageList()
            setHeaderUI()
        }
    }

    private fun setHeaderUI() = with(binding) {
        layoutChatHeader.apply {
            ivLeftBtn.visibility = View.VISIBLE
            ivLeftBtn.setOnClickListener { finish() }

            ivSecondRightBtn.visibility = View.VISIBLE
            ivSecondRightBtn.setImageResource(R.drawable.ic_24_info)

            tvMidText.visibility = View.VISIBLE
            tvMidText.text = viewModel.currentTPChannel.channelName
            tvMidText.setTextColor(Color.BLACK)
            tvMidText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
        }
    }

    companion object {
        const val EXTRA_TP_CHANNEL = "extra_tp_channel"
    }
}