package com.neptune.klat_uikit_android.feature.chat

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.extension.getSerializable
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
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
            if (viewModel.currentTPChannel.isFrozen) setFrozenUI() else setMessageBar()
            setHeaderUI()
        }
    }

    private fun setHeaderUI() = with(binding) {
        layoutChatHeader.apply {
            ivLeftBtn.visibility = View.VISIBLE
            ivLeftBtn.setOnClickListener { finish() }

            ivSecondRightBtn.visibility = View.VISIBLE
            ivSecondRightBtn.setImageResource(R.drawable.ic_24_info)
            ivSecondRightBtn.setOnClickListener {

            }

            tvMidText.visibility = View.VISIBLE
            tvMidText.text = viewModel.currentTPChannel.channelName
            tvMidText.setTextColor(Color.BLACK)
            tvMidText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
        }
    }

    private fun setFrozenUI() = with(binding) {
        clChatFrozen.visibility = View.VISIBLE
        layoutChatMessageBar.etInputMessage.isEnabled = false
        layoutChatMessageBar.etInputMessage.hint = "메세지 입력 불가"
        layoutChatMessageBar.etInputMessage.setHintTextColor(Color.parseColor("#9A9A9A"))
        layoutChatMessageBar.ivChatAttach.setColorFilter(Color.parseColor("#C1C1C1"))
    }

    private fun setMessageBar() = with(binding) {
        layoutChatMessageBar.etInputMessage.addTextChangedListener { input ->
            layoutChatMessageBar.ivChatSend.isVisible = !input.isNullOrEmpty()
        }

        layoutChatMessageBar.ivChatSend.setOnClickListener {
            // TODO viewModel.sendMessage()
        }

        layoutChatMessageBar.ivChatAttach.setOnClickListener {
            viewModel.setAttachMode(!viewModel.isAttachMode)

            layoutChatMessageBar.ivChatAttach
                .loadThumbnail(if(viewModel.isAttachMode) R.drawable.ic_24_attach__close else R.drawable.ic_24_attach)

            when (viewModel.isAttachMode) {
                true -> layoutChatAttach.clChatAttachBloack.visibility = View.VISIBLE
                false -> layoutChatAttach.clChatAttachBloack.visibility = View.GONE
            }
        }
    }

    companion object {
        const val EXTRA_TP_CHANNEL = "extra_tp_channel"
    }
}