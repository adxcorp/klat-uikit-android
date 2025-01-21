package com.neptune.klat_uikit_android.core.base

import android.net.Uri
import io.talkplus.entity.channel.TPChannel

object ChannelObject {
    lateinit var tpChannel: TPChannel
        private set

    var userId: String = ""

    var tag: String = ""

    var photoUri: Uri? = null
        private set

    fun setPhotoUri(uri: Uri) {
        photoUri = uri
    }

    fun setTPChannel(channel: TPChannel) {
        tpChannel = channel
    }
}