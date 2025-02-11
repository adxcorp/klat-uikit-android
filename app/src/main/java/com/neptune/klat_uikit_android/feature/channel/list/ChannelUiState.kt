package com.neptune.klat_uikit_android.feature.channel.list

import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.data.model.channel.ChannelListResponse
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMember
import io.talkplus.entity.channel.TPMessage

sealed class ChannelUiState {
    data class BaseState(val baseState: BaseUiState) : ChannelUiState()
    data class GetChannelList(val channelList: ChannelListResponse) : ChannelUiState()
    data class GetChannel(val tpChannel: TPChannel) : ChannelUiState()
    data class ReceivedMessage(val tpChannel: TPChannel, val tpMessage: TPMessage) : ChannelUiState()
    data class AddedChannel(val tpChannel: TPChannel) : ChannelUiState()
    data class RemovedChannel(val tpChannel: TPChannel) : ChannelUiState()
    data class ChangedChannel(val tpChannel: TPChannel) : ChannelUiState()
    data class LeaveChannel(val tpChannel: TPChannel) : ChannelUiState()
    data class BanUser(val tpChannel: TPChannel) : ChannelUiState()
    data class AddMember(val tpChannel: TPChannel) : ChannelUiState()
    object MarkAsRead : ChannelUiState()
    object ChannelListEmpty : ChannelUiState()
}