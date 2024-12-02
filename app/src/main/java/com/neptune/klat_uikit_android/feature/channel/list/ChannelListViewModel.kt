package com.neptune.klat_uikit_android.feature.channel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChannelListViewModel(private val channelRepository: ChannelRepository = ChannelRepository()) : ViewModel() {
    private var _channelUiState = MutableSharedFlow<ChannelUiState>()
    val channelUiState: SharedFlow<ChannelUiState>
        get() = _channelUiState.asSharedFlow()

    val currentChannelList: ArrayList<TPChannel> = arrayListOf()

    fun getChannelList(lastChannel: TPChannel? = null) {
        viewModelScope.launch {
            _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.Loading))
            channelRepository.getChannelList(lastChannel).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> {
                        _channelUiState.emit(ChannelUiState.GetChannelList(callbackResult.successData))
                        currentChannelList.addAll(callbackResult.successData.first)
                    }
                    is Result.Failure -> _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
            _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.LoadingFinish))
        }
    }
}