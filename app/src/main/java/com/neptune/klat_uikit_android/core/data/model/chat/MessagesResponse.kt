package com.neptune.klat_uikit_android.core.data.model.chat

import io.talkplus.entity.channel.TPMessage

data class MessagesResponse(
    val tpMessages: List<TPMessage>,
    val hasNext: Boolean
)
