package com.neptune.klat_uikit_android.feature.channel.create

import com.neptune.klat_uikit_android.core.base.BaseUiState

sealed class CreateChannelUiState {
    data class BaseState(val baseState: BaseUiState) : CreateChannelUiState()
    object CreateChannel : CreateChannelUiState()
    object UpdateChannel : CreateChannelUiState()
}