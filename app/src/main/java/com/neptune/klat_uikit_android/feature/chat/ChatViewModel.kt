package com.neptune.klat_uikit_android.feature.chat

import android.content.ClipData
import android.content.ClipboardManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.channel.EventType
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import com.neptune.klat_uikit_android.core.data.repository.chat.ChatRepository
import com.neptune.klat_uikit_android.core.data.repository.event.EventRepository
import io.talkplus.entity.channel.TPMessage
import io.talkplus.params.TPMessageRetrievalParams
import io.talkplus.params.TPMessageSendParams
import io.talkplus.params.TPMessageSendParams.ContentType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class ChatViewModel(
    private val chatRepository: ChatRepository = ChatRepository(),
    private val eventRepository: EventRepository = EventRepository(),
    private val channelRepository: ChannelRepository = ChannelRepository()
) : ViewModel() {
    private var photoFile: File? = null
    private var hasNext: Boolean = true

    val tag = ChannelObject.tpChannel.channelId

    var isOnStop = false

    var isFirstLoad: Boolean = true
        private set

    var currentItemCount = 0

    var isAttachMode: Boolean = false
        private set

    var longClickPosition: Int = 0
        private set

    private var _chatUiState = MutableSharedFlow<ChatUiState>()
    val channelUiState: SharedFlow<ChatUiState>
        get() = _chatUiState.asSharedFlow()

    private var clickedTPMessage: TPMessage = TPMessage(JsonObject())

    private val tpMessages: ArrayList<TPMessage> = arrayListOf()

    var isMyLastMessage: Boolean = false
        private set

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

                    is Result.Failure -> _chatUiState.emit(ChatUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun sendMessage(
        message: String,
        contentType: ContentType = ContentType.TEXT
    ) {
        val params: TPMessageSendParams = TPMessageSendParams.Builder(ChannelObject.tpChannel,
            TPMessageSendParams.MessageType.TEXT,
            contentType
        )
            .setText(message)
            .setFile(photoFile)
            .build()

        viewModelScope.launch {
            chatRepository.sendMessage(params).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _chatUiState.emit(ChatUiState.SendMessage(callbackResult.successData))
                    is Result.Failure -> _chatUiState.emit(ChatUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun deleteMessage() {
        viewModelScope.launch {
            chatRepository.deleteMessage(clickedTPMessage).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _chatUiState.emit(ChatUiState.DeleteMessage(clickedTPMessage))
                    is Result.Failure -> _chatUiState.emit(ChatUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun observeEvent() {
        viewModelScope.launch {
            eventRepository.observeChannel(tag).collect { callbackResult ->
                when(callbackResult.type) {
                    EventType.BAN_USER -> Unit
                    EventType.ADDED_CHANNEL -> Unit
                    EventType.LEAVE_OTHER_USER -> Unit
                    EventType.ADD_MEMBER -> Unit
                    EventType.CHANGED_CHANNEL -> _chatUiState.emit(ChatUiState.ChannelChanged(callbackResult.channel.isFrozen))
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
                targetMessage = clickedTPMessage,
                selectedEmoji = selectedEmoji
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> Unit // addReaction 성공시 updateReaction 콜백으로 수신됩니다.
                    is Result.Failure -> _chatUiState.emit(ChatUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    private fun removeReaction(selectedEmoji: String) {
        viewModelScope.launch {
            chatRepository.removeMessageReaction(
                targetMessage = clickedTPMessage,
                selectedEmoji = selectedEmoji
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> Unit
                    is Result.Failure -> _chatUiState.emit(ChatUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    private fun removeAndAddReaction(removeEmoji: String, addEmoji: String) {
        viewModelScope.launch {
            chatRepository.removeMessageReaction(
                targetMessage = clickedTPMessage,
                selectedEmoji = removeEmoji
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> addReaction(addEmoji)
                    is Result.Failure -> _chatUiState.emit(ChatUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun updateReaction(selectedEmoji: String) {
        val reactedEmoji: String? = getUserReactionEmoji(clickedTPMessage)

        viewModelScope.launch {
            when (reactedEmoji) {
                null -> addReaction(selectedEmoji = selectedEmoji)
                selectedEmoji -> removeReaction(selectedEmoji = selectedEmoji)
                else -> removeAndAddReaction(removeEmoji = reactedEmoji, addEmoji = selectedEmoji)
            }
        }
    }

    fun markAsRead() {
        viewModelScope.launch {
            channelRepository.markAsRead().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _chatUiState.emit(ChatUiState.MarkAsRead)
                    is Result.Failure -> _chatUiState.emit(ChatUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
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

    fun sendFileMessage(photoFile: File) {
        this.photoFile = photoFile
        isMyLastMessage = true
        sendMessage(
            message = "사진을 보냈습니다.",
            contentType = ContentType.FILE
        )
        this.photoFile = null
    }

    fun copyMessage(clipboard: ClipboardManager) {
        val clip = ClipData.newPlainText("Copied Text", clickedTPMessage.text)
        clipboard.setPrimaryClip(clip)
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

    fun setMyLastMessage(myMessage: Boolean) {
        isMyLastMessage = myMessage
    }
}