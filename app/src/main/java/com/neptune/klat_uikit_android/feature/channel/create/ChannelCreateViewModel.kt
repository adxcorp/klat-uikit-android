package com.neptune.klat_uikit_android.feature.channel.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class ChannelCreateViewModel(private val channelRepository: ChannelRepository = ChannelRepository()) : ViewModel() {
    private var photoFile: File? = null
    private var channelName: String = ""
    private var memberCount: Int = 0

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
            channelRepository.createChannel(
                memberCount = memberCount,
                channelName = channelName,
                photoFile = photoFile
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _createChannelUiState.emit(CreateChannelUiState.CreateChannel)
                    is Result.Failure -> { }
                }
            }
        }
    }

    private fun updateChannel() {
        viewModelScope.launch {
            channelRepository.updateChannel(
                channelName = channelName,
                photoFile = photoFile
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _createChannelUiState.emit(CreateChannelUiState.UpdateChannel)
                    is Result.Failure -> { }
                }
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