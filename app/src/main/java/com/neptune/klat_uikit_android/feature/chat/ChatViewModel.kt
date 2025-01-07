package com.neptune.klat_uikit_android.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.channel.EventType
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import com.neptune.klat_uikit_android.core.data.repository.chat.ChatRepository
import com.neptune.klat_uikit_android.feature.channel.list.ChannelUiState
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import io.talkplus.params.TPMessageRetrievalParams
import io.talkplus.params.TPMessageSendParams
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository = ChatRepository()) : ViewModel() {
    private var hasNext: Boolean = true

    var currentPosition = 0
        private set

    var isFirstLoad: Boolean = true
        private set

    var isAttachMode: Boolean = false
        private set

    private val tpMessages: ArrayList<TPMessage> = arrayListOf()

    private var _chatUiState = MutableSharedFlow<ChatUiState>()
    val channelUiState: SharedFlow<ChatUiState>
        get() = _chatUiState.asSharedFlow()

    fun getMessageList() {
        if (!hasNext) return

        val params: TPMessageRetrievalParams = TPMessageRetrievalParams.Builder(ChannelObject.tpChannel)
            .setLastMessage(tpMessages.lastOrNull())
            .build()

        viewModelScope.launch {
            chatRepository.getMessageList(params).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> {
                        hasNext = callbackResult.successData.hasNext
                        tpMessages.addAll(callbackResult.successData.tpMessages)
                        _chatUiState.emit(ChatUiState.GetMessages(callbackResult.successData.tpMessages))
                    }

                    is Result.Failure -> {

                    }
                }
            }
        }
    }

    fun sendMessage(message: String) {
        val params: TPMessageSendParams = TPMessageSendParams.Builder(ChannelObject.tpChannel,
            TPMessageSendParams.MessageType.TEXT,
            TPMessageSendParams.ContentType.TEXT)
            .setText(message)
            .build()

        viewModelScope.launch {
            chatRepository.sendMessage(params).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _chatUiState.emit(ChatUiState.SendMessage(callbackResult.successData))
                    is Result.Failure -> { }
                }
            }
        }
    }

    fun receiveMessage() {
        viewModelScope.launch {
            chatRepository.receiveMessage().collect { tpMessage ->
                _chatUiState.emit(ChatUiState.ReceiveMessage(tpMessage))
            }
//            ChannelObject.channelRepository.observeChannel().collect { callbackResult ->
//                when(callbackResult.type) {
//                    EventType.BAN_USER -> {}
//                    EventType.CHANGED_CHANNEL -> {}
//                    EventType.ADDED_CHANNEL -> {}
//                    EventType.REMOVED_CHANNEL -> {}
//                    EventType.RECEIVED_MESSAGE -> {
//                        _chatUiState.emit(ChatUiState.ReceiveMessage(callbackResult.message ?: error("null")))
//                    }
//                }
//            }
        }
    }

    fun setAttachMode(mode: Boolean) {
        isAttachMode = mode
    }

    fun setFirstLoad(isLoaded: Boolean) {
        isFirstLoad = isLoaded
    }

    fun setPosition(position: Int) {
        currentPosition = position
    }
}