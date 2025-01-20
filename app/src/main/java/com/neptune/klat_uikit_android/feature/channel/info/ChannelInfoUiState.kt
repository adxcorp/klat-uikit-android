package com.neptune.klat_uikit_android.feature.channel.info

import com.neptune.klat_uikit_android.core.base.BaseUiState

sealed class ChannelInfoUiState {
    data class BaseState(val baseState: BaseUiState) : ChannelInfoUiState()
    object Frozen : ChannelInfoUiState()
    object UnFrozen : ChannelInfoUiState()
    object LeaveChannel : ChannelInfoUiState()
    object RemoveChannel : ChannelInfoUiState()
    object EnablePush : ChannelInfoUiState()
    object DisablePush : ChannelInfoUiState()
}