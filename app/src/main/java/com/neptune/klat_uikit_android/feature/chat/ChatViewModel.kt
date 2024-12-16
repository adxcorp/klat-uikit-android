package com.neptune.klat_uikit_android.feature.chat

import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    private var currentChannelId: String = ""

    fun setChannel(channelId: String) {
        currentChannelId = channelId
    }

    private fun getChannel() {

    }
}