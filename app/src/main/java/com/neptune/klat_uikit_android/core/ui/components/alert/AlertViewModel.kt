package com.neptune.klat_uikit_android.core.ui.components.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.user.UserEventRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val userEventRepository: UserEventRepository = UserEventRepository()) : ViewModel() {
    private var _alertUiState = MutableSharedFlow<AlertUiState>()
    val alertUiState: SharedFlow<AlertUiState>
        get() = _alertUiState.asSharedFlow()

    val isChannelOwner: Boolean = ChannelObject.userId == ChannelObject.tpChannel.channelOwnerId

    fun banUser(targetId: String) {
        viewModelScope.launch {
            userEventRepository.banUser(targetId).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _alertUiState.emit(AlertUiState.BanUser)
                    is Result.Failure -> _alertUiState.emit(AlertUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun grantOwner(targetId: String) {
        viewModelScope.launch {
            userEventRepository.grantOwner(targetId).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _alertUiState.emit(AlertUiState.GrantOwner)
                    is Result.Failure -> _alertUiState.emit(AlertUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun muteUser(targetId: String) {
        viewModelScope.launch {
            userEventRepository.muteUser(targetId).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _alertUiState.emit(AlertUiState.MuteUser)
                    is Result.Failure -> _alertUiState.emit(AlertUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun peerMuteUser(targetId: String) {
        viewModelScope.launch {
            userEventRepository.peerMuteUser(targetId).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _alertUiState.emit(AlertUiState.PeerMuteUser)
                    is Result.Failure -> _alertUiState.emit(AlertUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }
}