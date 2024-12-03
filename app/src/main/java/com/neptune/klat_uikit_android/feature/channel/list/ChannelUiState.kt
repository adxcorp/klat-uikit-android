package com.neptune.klat_uikit_android.feature.channel.list

import com.neptune.klat_uikit_android.core.base.BaseUiState
import io.talkplus.entity.channel.TPChannel

sealed class ChannelUiState {
    data class BaseState(val baseState: BaseUiState) : ChannelUiState()
    data class GetChannelList(val channelList: Pair<List<TPChannel>, Boolean>) : ChannelUiState()
    object ChannelListEmpty : ChannelUiState()
}