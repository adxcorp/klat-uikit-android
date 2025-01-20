package com.neptune.klat_uikit_android.core.data.repository.event

import android.util.Log
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.channel.EventType
import com.neptune.klat_uikit_android.core.data.model.channel.ObserveChannelResponse
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMember
import io.talkplus.entity.channel.TPMessage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class EventRepository {
    fun observeChannel(tag: String): Flow<ObserveChannelResponse> = callbackFlow {
        TalkPlus.addChannelListener(tag, object : TalkPlus.ChannelListener {
            override fun onMessageReceived(tpChannel: TPChannel, tpMessage: TPMessage) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(
                    ObserveChannelResponse(
                    type = EventType.RECEIVED_MESSAGE,
                    channel = tpChannel,
                    message = tpMessage
                ))
            }

            override fun onUpdatedReaction(tpChannel: TPChannel, tpMessage: TPMessage) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(
                    ObserveChannelResponse(
                        type = EventType.UPDATED_REACTION,
                        channel = tpChannel,
                        message = tpMessage
                ))
            }

            override fun onChannelChanged(tpChannel: TPChannel) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(
                    ObserveChannelResponse(
                    type = EventType.CHANGED_CHANNEL,
                    channel = tpChannel,
                ))
            }

            override fun onChannelAdded(tpChannel: TPChannel) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(
                    ObserveChannelResponse(
                    type = EventType.ADDED_CHANNEL,
                    channel = tpChannel,
                ))
            }

            override fun onChannelRemoved(tpChannel: TPChannel) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(
                    ObserveChannelResponse(
                    type = EventType.REMOVED_CHANNEL,
                    channel = tpChannel,
                ))
            }

            override fun onMemberBanned(tpChannel: TPChannel, users: MutableList<TPMember>) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(
                    ObserveChannelResponse(
                    type = EventType.BAN_USER,
                    channel = tpChannel,
                ))
            }

            override fun onMemberLeft(tpChannel: TPChannel, users: MutableList<TPMember>) {
                trySend(ObserveChannelResponse(
                    type = if (isContainsMe(tpChannel)) EventType.LEAVE_OTHER_USER else EventType.LEAVE_CHANNEL,
                    channel = tpChannel
                ))
            }
        })
        awaitClose { cancel() }
    }

    private fun isContainsMe(tpChannel: TPChannel): Boolean {
        tpChannel.members.forEach { tpMember ->
            if (tpMember.userId == ChannelObject.userId) return true
        }
        return false
    }
}