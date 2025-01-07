package com.neptune.klat_uikit_android.core.ui.components.alert

import com.neptune.klat_uikit_android.core.base.BaseUiState
import io.talkplus.entity.channel.TPChannel

sealed class AlertUiState {
    data class BaseState(val baseState: BaseUiState) : AlertUiState()
    data class BanUser(val tpChannel: TPChannel) : AlertUiState()
    object MuteUser : AlertUiState()
    object UnMuteUser : AlertUiState()
    object PeerMuteUser : AlertUiState()
    object PeerUnMuteUser : AlertUiState()
    object GrantOwner : AlertUiState()
}