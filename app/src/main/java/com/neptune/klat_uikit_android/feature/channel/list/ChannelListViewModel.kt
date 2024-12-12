package com.neptune.klat_uikit_android.feature.channel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.channel.EventType
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChannelListViewModel(private val channelRepository: ChannelRepository = ChannelRepository()) : ViewModel() {
    init {
        observeChannel()
    }

    private var _channelUiState = MutableSharedFlow<ChannelUiState>()
    val channelUiState: SharedFlow<ChannelUiState>
        get() = _channelUiState.asSharedFlow()

    val currentChannelList: ArrayList<TPChannel> = arrayListOf()

    var currentTPChannel: TPChannel? = null
        private set

    private var hasNext: Boolean = true

    val tag: String = "ffbdd92b-c437-4c84-ab2c-9bf2c9207a42"

    fun getChannelList(lastChannel: TPChannel? = currentTPChannel) {
        if (hasNext) {
            viewModelScope.launch {
                _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.Loading))

                channelRepository.getChannelList(lastChannel).collect { callbackResult ->
                    when (callbackResult) {
                        is Result.Success -> {
                            if (callbackResult.successData.tpChannels.isEmpty()) {
                                currentChannelList.addAll(callbackResult.successData.tpChannels)
                                currentTPChannel = callbackResult.successData.tpChannels.last()
                                _channelUiState.emit(ChannelUiState.GetChannelList(callbackResult.successData))
                            }

                            if (currentChannelList.isEmpty()) {
                                _channelUiState.emit(ChannelUiState.ChannelListEmpty)
                            }

                            hasNext = callbackResult.successData.hasNext
                        }

                        is Result.Failure -> _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                    }
                }

                _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.LoadingFinish))
            }
        }
    }

    private fun observeChannel() {
        viewModelScope.launch {
            channelRepository.observeChannel(tag).collect { callbackResult ->
                when(callbackResult.type) {
                    EventType.RECEIVED_MESSAGE -> {}
                    EventType.CHANGED_CHANNEL -> {}
                    EventType.ADDED_CHANNEL -> {}
                    EventType.REMOVED_CHANNEL -> {}
                }
            }
        }
    }
}