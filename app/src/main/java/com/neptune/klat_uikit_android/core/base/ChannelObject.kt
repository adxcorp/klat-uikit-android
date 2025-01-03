package com.neptune.klat_uikit_android.core.base

import io.talkplus.entity.channel.TPChannel

object ChannelObject {
    lateinit var tpChannel: TPChannel
        private set

    var userId: String = ""
    var ownerId: String = ""

    var tag: String = ""

    fun setTPChannel(channel: TPChannel) {
        tpChannel = channel
    }
}