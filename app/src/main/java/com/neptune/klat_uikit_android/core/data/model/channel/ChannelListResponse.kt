package com.neptune.klat_uikit_android.core.data.model.channel

import io.talkplus.entity.channel.TPChannel

data class ChannelListResponse(
    val tpChannels: List<TPChannel>,
    val hasNext: Boolean
)
