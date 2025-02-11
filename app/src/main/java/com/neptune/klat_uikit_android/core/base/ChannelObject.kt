package com.neptune.klat_uikit_android.core.base

import android.net.Uri
import com.google.gson.JsonObject
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage

object ChannelObject {
    var tpChannel: TPChannel = TPChannel(JsonObject())
        private set

    var tpMessage: TPMessage = TPMessage(JsonObject())
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

    fun setTPMessage(message: TPMessage) {
        tpMessage = message
    }
}