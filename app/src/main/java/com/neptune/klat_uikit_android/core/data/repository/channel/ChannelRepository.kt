package com.neptune.klat_uikit_android.core.data.repository.channel

import android.util.Log
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult
import com.neptune.klat_uikit_android.core.data.model.channel.ChannelListResponse
import com.neptune.klat_uikit_android.core.data.model.channel.EventType
import com.neptune.klat_uikit_android.core.data.model.channel.ObserveChannelResponse
import io.talkplus.TalkPlus
import io.talkplus.TalkPlus.ChannelListener
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMember
import io.talkplus.entity.channel.TPMessage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class ChannelRepository {
    fun getChannelList(lastChannel: TPChannel?): Flow<Result<ChannelListResponse, WrappedFailResult>> {
        return callbackFlow {
            TalkPlus.getChannels(lastChannel, object : TalkPlus.TPCallbackListener<List<TPChannel>, Boolean> {
                override fun onSuccess(tpChannelList: List<TPChannel>, hasNext: Boolean) {
                    trySend(Result.Success(ChannelListResponse(
                        tpChannels = tpChannelList.filter { it.type == "private" },
                        hasNext = hasNext
                    )))
                }

                override fun onFailure(errorCode: Int, exception: Exception) {
                    trySend(Result.Failure(WrappedFailResult(
                        errorCode = errorCode,
                        exception = exception
                    )))
                }
            })
            awaitClose { cancel() }
        }
    }

    fun observeChannel(): Flow<ObserveChannelResponse> = callbackFlow {
        TalkPlus.addChannelListener(ChannelObject.tag, object : ChannelListener {
            override fun onMessageReceived(tpChannel: TPChannel, tpMessage: TPMessage) {
                trySend(ObserveChannelResponse(
                    type = EventType.RECEIVED_MESSAGE,
                    channel = tpChannel,
                    message = tpMessage
                ))
            }

            override fun onChannelChanged(tpChannel: TPChannel) {
                trySend(ObserveChannelResponse(
                    type = EventType.CHANGED_CHANNEL,
                    channel = tpChannel,
                ))
            }

            override fun onChannelAdded(tpChannel: TPChannel) {
                trySend(ObserveChannelResponse(
                    type = EventType.ADDED_CHANNEL,
                    channel = tpChannel,
                ))
            }

            override fun onChannelRemoved(tpChannel: TPChannel) {
                trySend(ObserveChannelResponse(
                    type = EventType.REMOVED_CHANNEL,
                    channel = tpChannel,
                ))
            }

            override fun onMemberBanned(tpChannel: TPChannel, users: MutableList<TPMember>) {
                trySend(ObserveChannelResponse(
                    type = EventType.BAN_USER,
                    channel = tpChannel,
                ))
            }
        })
        awaitClose { cancel() }
    }
}