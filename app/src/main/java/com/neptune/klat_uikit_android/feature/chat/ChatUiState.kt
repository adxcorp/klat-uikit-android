package com.neptune.klat_uikit_android.feature.chat

sealed class ChatUiState {
    data class BaseState(val baseState: ChatUiState) : ChatUiState()
    object GetMessages : ChatUiState()
}