package com.neptune.klat_uikit_android.feature.chat

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.getSerializable
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.ui.ProfileDialog
import com.neptune.klat_uikit_android.databinding.ActivityChatBinding
import com.neptune.klat_uikit_android.feature.channel.info.ChannelInfoActivity
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private val viewModel: ChatViewModel by viewModels()
    private val adapter: ChatAdapter by lazy { setAdapter() }

    private val onLayoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
        if (bottom < oldBottom) {
            binding.rvChat.scrollBy(0, oldBottom - bottom)
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            val isAtTop = !recyclerView.canScrollVertically(-1)

            if (isAtTop) {
                viewModel.getMessageList()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        viewModel.getMessageList()
        viewModel.receiveMessage()
        setHeaderUI()
        setRecyclerViewListener()
        observeChatUiState()
        if (ChannelObject.tpChannel.isFrozen) setFrozenUI() else setMessageBarUI()
    }

    private fun observeChatUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.channelUiState.collect { chatUiState ->
                    when (chatUiState) {
                        is ChatUiState.BaseState -> {

                        }

                        is ChatUiState.SendMessage -> sendMessage(chatUiState.tpMessage)
                        is ChatUiState.GetMessages -> loadMessages(chatUiState.tpMessages)
                        is ChatUiState.ReceiveMessage -> receiveMessage(chatUiState.tpMessage)
                    }
                }
            }
        }
    }

    private fun setHeaderUI() = with(binding) {
        layoutChatHeader.apply {
            ivLeftBtn.visibility = View.VISIBLE
            ivLeftBtn.setOnClickListener { finish() }

            ivSecondRightBtn.visibility = View.VISIBLE
            ivSecondRightBtn.setImageResource(R.drawable.ic_24_info)
            ivSecondRightBtn.setOnClickListener {
                val intent = Intent(this@ChatActivity, ChannelInfoActivity::class.java)
                startActivity(intent)
            }

            tvMidText.visibility = View.VISIBLE
            tvMidText.text = ChannelObject.tpChannel.channelName
            tvMidText.setTextColor(Color.BLACK)
            tvMidText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
        }
    }

    private fun setFrozenUI() = with(binding.layoutChatMessageBar) {
        binding.clChatFrozen.visibility = View.VISIBLE
        etInputMessage.isEnabled = false
        etInputMessage.hint = "메세지 입력 불가"
        etInputMessage.setHintTextColor(Color.parseColor("#9A9A9A"))
        ivChatAttach.setColorFilter(Color.parseColor("#C1C1C1"))
    }

    private fun setMessageBarUI() = with(binding.layoutChatMessageBar) {
        setAttackBlockUI()

        etInputMessage.addTextChangedListener { input ->
            ivChatSend.isVisible = !input.isNullOrEmpty()
        }

        ivChatSend.setOnClickListener {
            viewModel.sendMessage(etInputMessage.text.toString())
            etInputMessage.setText("")
            binding.rvChat.scrollToPosition(BOTTOM)
        }

        ivChatAttach.setOnClickListener {
            viewModel.setAttachMode(!viewModel.isAttachMode)

            ivChatAttach.loadThumbnail(if(viewModel.isAttachMode) R.drawable.ic_24_attach__close else R.drawable.ic_24_attach)

            when (viewModel.isAttachMode) {
                true -> binding.layoutChatAttach.clChatAttachBloack.visibility = View.VISIBLE
                false -> binding.layoutChatAttach.clChatAttachBloack.visibility = View.GONE
            }
        }
    }

    private fun setAttackBlockUI() = with(binding.layoutChatAttach) {
        clChatAlbum.setOnClickListener {

        }

        clChatCamera.setOnClickListener {

        }

        clChatFile.setOnClickListener {

        }
    }

    private fun setRecyclerViewListener() = with(binding) {
        rvChat.adapter = this@ChatActivity.adapter
        rvChat.addOnScrollListener(onScrollListener)
        rvChat.addOnLayoutChangeListener(onLayoutChangeListener)
    }

    private fun setAdapter(): ChatAdapter {
        return ChatAdapter(
            tpMessages = arrayListOf(),
            tpChannel = ChannelObject.tpChannel,
            userId = ChannelObject.userId,
            context = this,
            onLongClickMessage = {

            },
            onClickProfile = { tpMessage, userId, ownerId ->
                ProfileDialog(
                    tpMessage =  tpMessage,
                    userId = userId,
                    ownerId = ownerId
                ).show(supportFragmentManager, null)
            }
        )
    }

    private fun loadMessages(newTPMessages: List<TPMessage>) {
        if (viewModel.isFirstLoad) {
            viewModel.setFirstLoad(false)
            binding.rvChat.scrollToPosition(BOTTOM)
        }
        adapter.addMessages(newTPMessages)
    }

    private fun sendMessage(tpMessage: TPMessage) {
        adapter.addMessage(tpMessage)
        binding.rvChat.scrollToPosition(BOTTOM)
    }

    private fun receiveMessage(tpMessage: TPMessage) {
        adapter.addMessage(tpMessage)
        val layoutManager = binding.rvChat.layoutManager as LinearLayoutManager
        if (layoutManager.findFirstVisibleItemPosition() == BOTTOM) {
            binding.rvChat.scrollToPosition(BOTTOM)
        }
    }

    companion object {
        const val EXTRA_TP_CHANNEL = "extra_tp_channel"
        private const val BOTTOM = 0
    }
}