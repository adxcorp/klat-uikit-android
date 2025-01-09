package com.neptune.klat_uikit_android.core.ui.components.profile

import com.neptune.klat_uikit_android.core.base.BaseUiState

sealed class ProfileUiState {
    data class BaseState(val baseState: BaseUiState) : ProfileUiState()
    object GetPeerMutedUsers : ProfileUiState()
    object PeerUnMuteUser : ProfileUiState()
    object UnMuteUser : ProfileUiState()
}