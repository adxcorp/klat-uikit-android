package com.neptune.klat_uikit_android.feature.channel.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.channel.EventType
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import com.neptune.klat_uikit_android.core.data.repository.event.EventRepository
import com.neptune.klat_uikit_android.core.ui.components.profile.ProfileUiState
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChannelListViewModel(
    private val channelRepository: ChannelRepository = ChannelRepository(),
    private val eventRepository: EventRepository = EventRepository()
) : ViewModel() {
    private var _channelUiState = MutableSharedFlow<ChannelUiState>()
    val channelUiState: SharedFlow<ChannelUiState>
        get() = _channelUiState.asSharedFlow()

    val currentChannelList: ArrayList<TPChannel> = arrayListOf()

    private var lastChannel: TPChannel? = null

    fun getChannels() {
        viewModelScope.launch {
            _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.Loading))

            channelRepository.getChannelList(lastChannel).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> {
                        lastChannel = callbackResult.successData.tpChannels.lastOrNull()
                        currentChannelList.addAll(callbackResult.successData.tpChannels)

                        when (callbackResult.successData.hasNext) {
                            true -> getChannels()
                            false -> _channelUiState.emit(ChannelUiState.GetChannelList(callbackResult.successData))
                        }

                        if (currentChannelList.isEmpty()) {
                            _channelUiState.emit(ChannelUiState.ChannelListEmpty)
                        }
                    }

                    is Result.Failure -> _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }

            _channelUiState.emit(ChannelUiState.BaseState(BaseUiState.LoadingFinish))
        }
    }

    fun observeChannelList() {
        viewModelScope.launch {
            eventRepository.observeChannel(ChannelObject.tag).collect { callbackResult ->
                when (callbackResult.type) {
                    EventType.UPDATED_REACTION -> Unit
                    EventType.LEAVE_OTHER_USER -> Unit
                    EventType.BAN_USER -> _channelUiState.emit(ChannelUiState.BanUser(callbackResult.channel))
                    EventType.CHANGED_CHANNEL -> _channelUiState.emit(ChannelUiState.ChangedChannel(callbackResult.channel))
                    EventType.ADDED_CHANNEL -> _channelUiState.emit(ChannelUiState.AddedChannel(callbackResult.channel))
                    EventType.REMOVED_CHANNEL -> _channelUiState.emit(ChannelUiState.RemovedChannel(callbackResult.channel))
                    EventType.LEAVE_CHANNEL -> _channelUiState.emit(ChannelUiState.LeaveChannel(callbackResult.channel))
                    EventType.RECEIVED_MESSAGE -> {
                        _channelUiState.emit(
                            ChannelUiState.ReceivedMessage(
                                tpMessage = callbackResult.message,
                                tpChannel = callbackResult.channel
                            )
                        )
                    }
                }
            }
        }
    }
}