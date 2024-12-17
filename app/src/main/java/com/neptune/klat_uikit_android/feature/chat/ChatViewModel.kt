package com.neptune.klat_uikit_android.feature.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import com.neptune.klat_uikit_android.core.data.repository.chat.ChatRepository
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import io.talkplus.params.TPMessageRetrievalParams
import kotlinx.coroutines.launch

class ChatViewModel(
    private val channelRepository: ChannelRepository = ChannelRepository(),
    private val chatRepository: ChatRepository = ChatRepository()
) : ViewModel() {

    lateinit var currentTPChannel: TPChannel
        private set

//    private var currentTPLastMessage: TPMessage? = null

    fun setTPChannel(tpChannel: TPChannel) {
        currentTPChannel = tpChannel
    }

    fun getMessageList() {
        val params: TPMessageRetrievalParams =  TPMessageRetrievalParams.Builder(currentTPChannel)
//            .setLastMessage(currentTPChannel.lastMessage)
            .build()

        viewModelScope.launch {
            chatRepository.getMessageList(params).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> {

                    }
                    is Result.Failure -> {

                    }
                }
            }
        }
    }
}