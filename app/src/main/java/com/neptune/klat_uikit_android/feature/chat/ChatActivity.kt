package com.neptune.klat_uikit_android.feature.chat

import android.graphics.Color
import android.os.Bundle
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
import com.neptune.klat_uikit_android.core.extension.getSerializable
import com.neptune.klat_uikit_android.core.extension.hideKeyboard
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ActivityChatBinding
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private val viewModel: ChatViewModel by viewModels()
    private val adapter: ChatAdapter by lazy { setAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        intent.getSerializable<TPChannel>(EXTRA_TP_CHANNEL)?.let { tpChannel ->
            viewModel.setTPChannel(tpChannel)
            viewModel.getMessageList()
            viewModel.receiveMessage()
            setHeaderUI()
            setRecyclerViewListener()
            observeChatUiState()
            if (viewModel.currentTPChannel.isFrozen) setFrozenUI() else setMessageBarUI()
        }
    }

    private fun observeChatUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.channelUiState.collect { chatUiState ->
                    when (chatUiState) {
                        is ChatUiState.BaseState -> {

                        }

                        is ChatUiState.SendMessage -> adapter.addMessage(chatUiState.tpMessage)
                        is ChatUiState.GetMessages -> loadMessages(chatUiState.tpMessages)
                        is ChatUiState.ReceiveMessage -> adapter.addMessage(chatUiState.tpMessage)
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

            }

            tvMidText.visibility = View.VISIBLE
            tvMidText.text = viewModel.currentTPChannel.channelName
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

        rvChat.setOnClickListener {
            hideKeyboard(binding.layoutChatMessageBar.etInputMessage)
        }

        rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val isAtTop = !recyclerView.canScrollVertically(-1)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    viewModel.setPosition(firstVisibleItemPosition)

                    if (isAtTop) {
                        viewModel.getMessageList()
                    }
                }
            }
        })
    }

    private fun setAdapter(): ChatAdapter {
        return ChatAdapter(
            tpMessages = arrayListOf(),
            tpChannel = viewModel.currentTPChannel,
            userId = "test2",
            context = this,
            onLongClickMessage = { },
            onClickProfile = { }
        )
    }

    private fun loadMessages(newTPMessages: List<TPMessage>) {
        if (viewModel.isFirstLoad) {
            viewModel.setFirstLoad(false)
            binding.rvChat.scrollToPosition(BOTTOM)
        }
        adapter.addMessages(newTPMessages)
    }

    companion object {
        const val EXTRA_TP_CHANNEL = "extra_tp_channel"
        private const val BOTTOM = 0 // reverseLayout
    }
}