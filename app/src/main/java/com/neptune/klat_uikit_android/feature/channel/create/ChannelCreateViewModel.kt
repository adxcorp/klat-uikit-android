package com.neptune.klat_uikit_android.feature.channel.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class ChannelCreateViewModel(private val channelRepository: ChannelRepository = ChannelRepository()) : ViewModel() {
    private var photoFile: File? = null
    private var memberCount: Int = 0
    var channelName: String = ""
        private set

    var channelType: String = ""
        private set

    private var _createChannelUiState = MutableSharedFlow<CreateChannelUiState>()
    val createChannelUiState: SharedFlow<CreateChannelUiState>
        get() = _createChannelUiState.asSharedFlow()

    fun upsert() {
        when (channelType) {
            ChannelCreateActivity.CREATE -> createChannel()
            ChannelCreateActivity.UPDATE -> updateChannel()
        }
    }

    private fun createChannel() {
        viewModelScope.launch {
            _createChannelUiState.emit(CreateChannelUiState.BaseState(BaseUiState.Loading))
            channelRepository.createChannel(
                memberCount = memberCount,
                channelName = channelName,
                photoFile = photoFile
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _createChannelUiState.emit(CreateChannelUiState.CreateChannel)
                    is Result.Failure -> _createChannelUiState.emit(CreateChannelUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
                _createChannelUiState.emit(CreateChannelUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }

    private fun updateChannel() {
        viewModelScope.launch {
            _createChannelUiState.emit(CreateChannelUiState.BaseState(BaseUiState.Loading))
            channelRepository.updateChannel(
                channelName = channelName,
                photoFile = photoFile
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _createChannelUiState.emit(CreateChannelUiState.UpdateChannel)
                    is Result.Failure -> _createChannelUiState.emit(CreateChannelUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
                _createChannelUiState.emit(CreateChannelUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }

    fun setPhotoFile(file: File) {
        photoFile = file
    }

    fun setChannelType(type: String) {
        channelType = type
    }

    fun setMemberCount(count: Int) {
        memberCount = count
    }

    fun setChannelName(name: String) {
        channelName = name
    }

    companion object {
        const val SUPER_TYPE = 100
    }
}