package com.neptune.klat_uikit_android.core.data.repository.chat

import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.chat.MessagesResponse
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult
import com.neptune.klat_uikit_android.core.data.model.channel.EventType
import com.neptune.klat_uikit_android.core.data.model.channel.ObserveChannelResponse
import io.talkplus.TalkPlus
import io.talkplus.TalkPlus.TPCallbackListener
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import io.talkplus.params.TPMessageRetrievalParams
import io.talkplus.params.TPMessageSendParams
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class ChatRepository {
    fun getMessageList(tpMessageRetrievalParams: TPMessageRetrievalParams): Flow<Result<MessagesResponse, WrappedFailResult>> {
        return callbackFlow {
            TalkPlus.getMessages(tpMessageRetrievalParams, object : TPCallbackListener<List<TPMessage>, Boolean> {
                override fun onSuccess(tpMessages: List<TPMessage>, hasNext: Boolean) {
                    trySend(Result.Success(MessagesResponse(
                        tpMessages = tpMessages,
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

    fun sendMessage(tpMessageSendParams: TPMessageSendParams): Flow<Result<TPMessage, WrappedFailResult>> {
        return callbackFlow {
            TalkPlus.sendMessage(tpMessageSendParams, object : TalkPlus.CallbackListener<TPMessage> {
                override fun onSuccess(tpMessage: TPMessage) {
                    trySend(Result.Success(tpMessage))
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

    fun addMessageReaction(
        targetMessage: TPMessage,
        selectedEmoji: String
    ): Flow<Result<TPMessage, WrappedFailResult>> {
        return callbackFlow {
            TalkPlus.addMessageReaction(
                targetMessage,
                selectedEmoji,
                object : TalkPlus.CallbackListener<TPMessage> {
                    override fun onSuccess(tpMessage: TPMessage) {
                        trySend(Result.Success(tpMessage))
                    }

                    override fun onFailure(errorCode: Int, exception: Exception) {
                        trySend(Result.Failure(WrappedFailResult(
                            errorCode = errorCode,
                            exception = exception
                        )))
                    }
                }
            )
            awaitClose { cancel() }
        }
    }

    fun removeMessageReaction(
        targetMessage: TPMessage,
        selectedEmoji: String
    ): Flow<Result<TPMessage, WrappedFailResult>> {
        return callbackFlow {
            TalkPlus.removeMessageReaction(
                targetMessage,
                selectedEmoji,
                object : TalkPlus.CallbackListener<TPMessage> {
                    override fun onSuccess(tpMessage: TPMessage) {
                        trySend(Result.Success(tpMessage))
                    }

                    override fun onFailure(errorCode: Int, exception: Exception) {
                        trySend(Result.Failure(WrappedFailResult(
                            errorCode = errorCode,
                            exception = exception
                        )))
                    }
                }
            )
            awaitClose { cancel() }
        }
    }


    fun receiveMessage(): Flow<TPMessage> = callbackFlow {
        TalkPlus.addChannelListener(ChannelObject.tpChannel.channelId, object : TalkPlus.ChannelListener {
            override fun onMessageReceived(tpChannel: TPChannel, tpMessage: TPMessage) {
                trySend(tpMessage)
            }
        })
        awaitClose { cancel() }
    }

    fun updatedReaction(): Flow<TPMessage> = callbackFlow {
        TalkPlus.addChannelListener(ChannelObject.tpChannel.channelId, object : TalkPlus.ChannelListener {
            override fun onUpdatedReaction(channel: TPChannel, tpMessage: TPMessage) {
                trySend(tpMessage)
            }
        })
        awaitClose { cancel() }
    }
}