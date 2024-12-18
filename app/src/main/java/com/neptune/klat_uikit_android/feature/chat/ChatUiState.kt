package com.neptune.klat_uikit_android.feature.chat

import io.talkplus.entity.channel.TPMessage

sealed class ChatUiState {
    data class BaseState(val baseState: ChatUiState) : ChatUiState()
    data class GetMessages(val tpMessages: List<TPMessage>) : ChatUiState()
}