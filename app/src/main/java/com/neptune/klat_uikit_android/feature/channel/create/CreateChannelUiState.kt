package com.neptune.klat_uikit_android.feature.channel.create

sealed class CreateChannelUiState {
    object CreateChannel : CreateChannelUiState()
    object UpdateChannel : CreateChannelUiState()
}