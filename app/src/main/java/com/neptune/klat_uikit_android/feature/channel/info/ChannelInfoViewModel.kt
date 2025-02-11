package com.neptune.klat_uikit_android.feature.channel.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChannelInfoViewModel(private val channelRepository: ChannelRepository = ChannelRepository()) : ViewModel() {
    private var _channelInfoUiState = MutableSharedFlow<ChannelInfoUiState>()
    val channelInfoUiState: SharedFlow<ChannelInfoUiState>
        get() = _channelInfoUiState.asSharedFlow()

    val isChannelOwner: Boolean = ChannelObject.tpChannel.channelOwnerId == ChannelObject.userId

    fun freezeChannel() {
        viewModelScope.launch {
            _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Loading))
            channelRepository.freezeChannel().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.Frozen)
                    is Result.Failure -> _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
                _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }

    fun unFreezeChannel() {
        viewModelScope.launch {
            _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Loading))
            channelRepository.unFreezeChannel().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.UnFrozen)
                    is Result.Failure -> _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
                _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }

    fun removeChannel() {
        viewModelScope.launch {
            _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Loading))
            channelRepository.removeChannel().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.RemoveChannel)
                    is Result.Failure -> _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
                _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }

    fun leaveChannel() {
        viewModelScope.launch {
            _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Loading))
            channelRepository.leaveChannel().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.LeaveChannel)
                    is Result.Failure -> _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
                _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }

    fun enablePush() {
        viewModelScope.launch {
            _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Loading))
            channelRepository.enablePush().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.EnablePush)
                    is Result.Failure -> _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
                _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }

    fun disablePush() {
        viewModelScope.launch {
            _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Loading))
            channelRepository.disablePush().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.DisablePush)
                    is Result.Failure -> _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
                _channelInfoUiState.emit(ChannelInfoUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }
}