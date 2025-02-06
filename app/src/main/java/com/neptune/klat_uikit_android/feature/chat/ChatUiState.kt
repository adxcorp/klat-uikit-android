package com.neptune.klat_uikit_android.feature.chat

import com.neptune.klat_uikit_android.core.base.BaseUiState
import io.talkplus.entity.channel.TPMessage

sealed class ChatUiState {
    data class BaseState(val baseState: BaseUiState) : ChatUiState()
    data class GetMessages(val tpMessages: List<TPMessage>) : ChatUiState()
    data class SendMessage(val tpMessage: TPMessage) : ChatUiState()
    data class ReceiveMessage(val tpMessage: TPMessage) : ChatUiState()
    data class DeleteMessage(val tpMessage: TPMessage) : ChatUiState()
    data class UpdatedReactionMessage(val tpMessage: TPMessage) : ChatUiState()
    data class ChannelChanged(val isFrozen: Boolean) : ChatUiState()
    object LeaveChannel : ChatUiState()
    object RemoveChannel : ChatUiState()
    object EmptyChat : ChatUiState()
    object MarkAsRead : ChatUiState()
}