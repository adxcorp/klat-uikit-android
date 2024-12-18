package com.neptune.klat_uikit_android.feature.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import com.neptune.klat_uikit_android.core.data.repository.chat.ChatRepository
import com.neptune.klat_uikit_android.feature.channel.list.ChannelUiState
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import io.talkplus.params.TPMessageRetrievalParams
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val channelRepository: ChannelRepository = ChannelRepository(),
    private val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {
    private var hasNext: Boolean = true

    lateinit var currentTPChannel: TPChannel
        private set

    var isFirstLoad: Boolean = true
        private set

    var isAttachMode: Boolean = false
        private set


    val tpMessages: ArrayList<TPMessage> = arrayListOf()

    private var _chatUiState = MutableSharedFlow<ChatUiState>()
    val channelUiState: SharedFlow<ChatUiState>
        get() = _chatUiState.asSharedFlow()

    fun getMessageList() {
        if (!hasNext) return

        val params: TPMessageRetrievalParams = TPMessageRetrievalParams.Builder(currentTPChannel)
            .setLastMessage(tpMessages.lastOrNull())
            .build()

        viewModelScope.launch {
            chatRepository.getMessageList(params).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> {
                        hasNext = callbackResult.successData.hasNext
                        tpMessages.addAll(callbackResult.successData.tpMessages)
                        _chatUiState.emit(ChatUiState.GetMessages)
                    }

                    is Result.Failure -> {

                    }
                }
            }
        }
    }

    fun setTPChannel(tpChannel: TPChannel) {
        currentTPChannel = tpChannel
    }

    fun setAttachMode(mode: Boolean) {
        isAttachMode = mode
    }

    fun setFirstLoad(isLoaded: Boolean) {
        isFirstLoad = isLoaded
    }
}