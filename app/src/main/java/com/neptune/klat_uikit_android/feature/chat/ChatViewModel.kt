package com.neptune.klat_uikit_android.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.channel.EventType
import com.neptune.klat_uikit_android.core.data.repository.chat.ChatRepository
import com.neptune.klat_uikit_android.core.data.repository.event.EventRepository
import io.talkplus.entity.channel.TPMessage
import io.talkplus.params.TPMessageRetrievalParams
import io.talkplus.params.TPMessageSendParams
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepository: ChatRepository = ChatRepository(),
    private val eventRepository: EventRepository = EventRepository()
) : ViewModel() {
    private var hasNext: Boolean = true

    var isFirstLoad: Boolean = true
        private set

    var isAttachMode: Boolean = false
        private set

    var longClickPosition: Int = 0
        private set

    private var clickedTPMessage: TPMessage? = null

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

                        if (callbackResult.successData.tpMessages.isEmpty()) {
                            _chatUiState.emit(ChatUiState.EmptyChat)
                            return@collect
                        }

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

    fun observeEvent() {
        viewModelScope.launch {
            eventRepository.observeChannel(ChannelObject.tpChannel.channelId).collect { callbackResult ->
                when(callbackResult.type) {
                    EventType.BAN_USER -> Unit
                    EventType.ADDED_CHANNEL -> Unit
                    EventType.LEAVE_OTHER_USER -> Unit
                    EventType.CHANGED_CHANNEL -> _chatUiState.emit(ChatUiState.Frozen(callbackResult.channel.isFrozen))
                    EventType.REMOVED_CHANNEL -> _chatUiState.emit(ChatUiState.RemoveChannel)
                    EventType.UPDATED_REACTION -> _chatUiState.emit(ChatUiState.UpdatedReactionMessage(callbackResult.message))
                    EventType.RECEIVED_MESSAGE -> _chatUiState.emit(ChatUiState.ReceiveMessage(callbackResult.message))
                    EventType.LEAVE_CHANNEL -> _chatUiState.emit(ChatUiState.LeaveChannel)
                }
            }
        }
    }

    private suspend fun addReaction(selectedEmoji: String) {
        viewModelScope.launch {
            chatRepository.addMessageReaction(
                targetMessage = clickedTPMessage ?: return@launch,
                selectedEmoji = selectedEmoji
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> Unit // addReaction 성공시 updateReaction 콜백으로 수신됩니다.
                    is Result.Failure -> {

                    }
                }
            }
        }
    }

    private fun removeReaction(selectedEmoji: String) {
        viewModelScope.launch {
            chatRepository.removeMessageReaction(
                targetMessage = clickedTPMessage ?: return@launch,
                selectedEmoji = selectedEmoji
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> Unit
                    is Result.Failure -> { }
                }
            }
        }
    }

    private fun removeAndAddReaction(removeEmoji: String, addEmoji: String) {
        viewModelScope.launch {
            chatRepository.removeMessageReaction(
                targetMessage = clickedTPMessage ?: return@launch,
                selectedEmoji = removeEmoji
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> addReaction(addEmoji)
                    is Result.Failure -> { }
                }
            }
        }
    }

    fun updateReaction(selectedEmoji: String) {
        val reactedEmoji: String? = clickedTPMessage?.let { tpMessage ->
            getUserReactionEmoji(tpMessage)
        }

        viewModelScope.launch {
            when (reactedEmoji) {
                null -> addReaction(selectedEmoji = selectedEmoji)
                selectedEmoji -> removeReaction(selectedEmoji = selectedEmoji)
                else -> removeAndAddReaction(removeEmoji = reactedEmoji, addEmoji = selectedEmoji)
            }
        }
    }

    private fun getUserReactionEmoji(tpMessage: TPMessage): String? {
        for ((key, mamValue) in tpMessage.reactions.entries) {
            if (mamValue.contains(ChannelObject.userId)) {
                return key
            }
        }
        return null
    }


    fun setClickedTPMessage(tpMessage: TPMessage) {
        clickedTPMessage = tpMessage
    }

    fun setAttachMode(mode: Boolean) {
        isAttachMode = mode
    }

    fun setFirstLoad(isLoaded: Boolean) {
        isFirstLoad = isLoaded
    }

    fun setLongClickPosition(position: Int) {
        longClickPosition = position
    }
}