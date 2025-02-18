package com.neptune.klat_uikit_android.core.data.model.channel

import com.google.gson.JsonObject
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage

data class ObserveChannelResponse(
    val type: EventType,
    val channel: TPChannel,
    val message: TPMessage = TPMessage(JsonObject())
)
