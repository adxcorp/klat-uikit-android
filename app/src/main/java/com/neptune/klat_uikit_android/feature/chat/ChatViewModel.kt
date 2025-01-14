package com.neptune.klat_uikit_android.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.chat.ChatRepository
import io.talkplus.entity.channel.TPMessage
import io.talkplus.params.TPMessageRetrievalParams
import io.talkplus.params.TPMessageSendParams
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository = ChatRepository()) : ViewModel() {
    private var hasNext: Boolean = true

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
            .setLastMessage(tpMessages.firstOrNull())
            .build()

        viewModelScope.launch {
            chatRepository.getMessageList(params).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> {
                        hasNext = callbackResult.successData.hasNext
                        val reversedMembers = callbackResult.successData.tpMessages.reversed()
                        tpMessages.addAll(0, reversedMembers)
                        _chatUiState.emit(ChatUiState.GetMessages(reversedMembers))
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
        }
    }

    fun addReaction(
        tpMessage: TPMessage,
        selectedEmoji: String
    ) {
        viewModelScope.launch {
            chatRepository.addMessageReaction(
                targetMessage = tpMessage,
                selectedEmoji = selectedEmoji
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> Unit // addReaction 성공시 updateReaction 콜백으로 수신됩니다.
                    is Result.Failure -> { }
                }
            }
        }
    }

    fun removeReaction(
        tpMessage: TPMessage,
        selectedEmoji: String
    ) {
        viewModelScope.launch {
            chatRepository.removeMessageReaction(
                targetMessage = tpMessage,
                selectedEmoji = selectedEmoji
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> Unit // removeReaction 성공시 updateReaction 콜백으로 수신됩니다.
                    is Result.Failure -> { }
                }
            }
        }
    }

    fun updateReaction() {
        viewModelScope.launch {
            chatRepository.updatedReaction().collect { tpMessage ->
                _chatUiState.emit(ChatUiState.UpdatedReactionMessage(tpMessage))
            }
        }
    }

    fun setAttachMode(mode: Boolean) {
        isAttachMode = mode
    }

    fun setFirstLoad(isLoaded: Boolean) {
        isFirstLoad = isLoaded
    }
}