package com.neptune.klat_uikit_android.feature.channel.info

import com.neptune.klat_uikit_android.feature.chat.ChatUiState

sealed class ChannelInfoUiState {
    data class BaseState(val baseState: ChatUiState) : ChannelInfoUiState()
    object Frozen : ChannelInfoUiState()
    object UnFrozen : ChannelInfoUiState()
    object LeaveChannel : ChannelInfoUiState()
    object RemoveChannel : ChannelInfoUiState()
    object EnablePush : ChannelInfoUiState()
    object DisablePush : ChannelInfoUiState()
}