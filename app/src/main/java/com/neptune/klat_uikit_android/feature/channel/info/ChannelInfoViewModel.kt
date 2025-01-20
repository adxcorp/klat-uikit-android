package com.neptune.klat_uikit_android.feature.channel.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun freezeChannel() {
        viewModelScope.launch {
            channelRepository.freezeChannel().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.Frozen)
                    is Result.Failure -> {}
                }
            }
        }
    }

    fun unFreezeChannel() {
        viewModelScope.launch {
            channelRepository.unFreezeChannel().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.UnFrozen)
                    is Result.Failure -> {}
                }
            }
        }
    }

    fun removeChannel() {
        viewModelScope.launch {
            channelRepository.removeChannel().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.RemoveChannel)
                    is Result.Failure -> {}
                }
            }
        }
    }

    fun leaveChannel() {
        viewModelScope.launch {
            channelRepository.leaveChannel().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.LeaveChannel)
                    is Result.Failure -> {}
                }
            }
        }
    }

    fun enablePush() {
        viewModelScope.launch {
            channelRepository.enablePush().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.EnablePush)
                    is Result.Failure -> {}
                }
            }
        }
    }

    fun disablePush() {
        viewModelScope.launch {
            channelRepository.disablePush().collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _channelInfoUiState.emit(ChannelInfoUiState.DisablePush)
                    is Result.Failure -> {}
                }
            }
        }
    }
}