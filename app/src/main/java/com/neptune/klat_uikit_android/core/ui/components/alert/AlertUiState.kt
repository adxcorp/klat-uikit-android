package com.neptune.klat_uikit_android.core.ui.components.alert

import com.neptune.klat_uikit_android.core.base.BaseUiState

sealed class AlertUiState {
    data class BaseState(val baseState: BaseUiState) : AlertUiState()
    object BanUser : AlertUiState()
    object MuteUser : AlertUiState()
    object PeerMuteUser : AlertUiState()
    object GrantOwner : AlertUiState()
}