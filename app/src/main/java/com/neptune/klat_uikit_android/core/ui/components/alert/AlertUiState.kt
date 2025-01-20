package com.neptune.klat_uikit_android.core.ui.components.alert

import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.feature.channel.list.ChannelUiState

sealed class AlertUiState {
    data class BaseState(val baseState: BaseUiState) : AlertUiState()
    object BanUser : AlertUiState()
    object MuteUser : AlertUiState()
    object PeerMuteUser : AlertUiState()
    object GrantOwner : AlertUiState()
    object RemoveChannel : AlertUiState()
    object LeaveChannel : AlertUiState()
}