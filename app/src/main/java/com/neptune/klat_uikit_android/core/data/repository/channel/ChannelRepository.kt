package com.neptune.klat_uikit_android.core.data.repository.channel

import android.util.Log
import com.google.gson.JsonObject
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult
import com.neptune.klat_uikit_android.core.data.model.channel.ChannelListResponse
import com.neptune.klat_uikit_android.feature.channel.create.ChannelCreateViewModel
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
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

    fun getChannel(): Flow<Result<TPChannel, WrappedFailResult>> = callbackFlow {
        TalkPlus.getChannel(ChannelObject.tpChannel.channelId, object : TalkPlus.CallbackListener<TPChannel> {
            override fun onSuccess(tpChannel: TPChannel) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(Result.Success(tpChannel))
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

    fun freezeChannel(): Flow<Result<Unit, WrappedFailResult>> = callbackFlow {
        TalkPlus.freezeChannel(ChannelObject.tpChannel.channelId, object : TalkPlus.CallbackListener<Void?> {
            override fun onSuccess(void: Void?) {
                trySend(Result.Success(Unit))
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

    fun unFreezeChannel(): Flow<Result<Unit, WrappedFailResult>> = callbackFlow {
        TalkPlus.unfreezeChannel(ChannelObject.tpChannel.channelId, object : TalkPlus.CallbackListener<Void?> {
            override fun onSuccess(void: Void?) {
                trySend(Result.Success(Unit))
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

    fun removeChannel(): Flow<Result<Unit, WrappedFailResult>> = callbackFlow {
        TalkPlus.deleteChannel(ChannelObject.tpChannel.channelId, object : TalkPlus.CallbackListener<Void?> {
            override fun onSuccess(void: Void?) {
                trySend(Result.Success(Unit))
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

    fun leaveChannel(): Flow<Result<Unit, WrappedFailResult>> = callbackFlow {
        TalkPlus.leaveChannel(ChannelObject.tpChannel, true, object : TalkPlus.CallbackListener<Void?> {
            override fun onSuccess(void: Void?) {
                trySend(Result.Success(Unit))
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

    fun enablePush(): Flow<Result<TPChannel, WrappedFailResult>> = callbackFlow {
        TalkPlus.enableChannelPushNotification(ChannelObject.tpChannel, object : TalkPlus.CallbackListener<TPChannel> {
            override fun onSuccess(tpChannel: TPChannel) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(Result.Success(tpChannel))
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

    fun disablePush(): Flow<Result<TPChannel, WrappedFailResult>> = callbackFlow {
        TalkPlus.disableChannelPushNotification(ChannelObject.tpChannel, object : TalkPlus.CallbackListener<TPChannel> {
            override fun onSuccess(tpChannel: TPChannel) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(Result.Success(tpChannel))
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

    fun markAsRead(): Flow<Result<TPChannel, WrappedFailResult>> = callbackFlow {
        TalkPlus.markAsReadChannel(ChannelObject.tpChannel, object : TalkPlus.CallbackListener<TPChannel> {
            override fun onSuccess(tpChannel: TPChannel) {
                ChannelObject.setTPChannel(tpChannel)
                trySend(Result.Success(tpChannel))
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

    fun createChannel(
        memberCount: Int,
        channelName: String,
        photoFile: File? = null,
        category: String = "",
        subcategory: String = "",
        targetIds: List<String> = listOf()
    ): Flow<Result<TPChannel, WrappedFailResult>> = callbackFlow {
        TalkPlus.createChannel(
            targetIds,
            null,
            false,
            memberCount,
            false,
            if (memberCount == ChannelCreateViewModel.SUPER_TYPE) "super_private" else "private",
            channelName,
            "",
            category,
            subcategory,
            "",
            JsonObject(),
            photoFile,
            object : TalkPlus.CallbackListener<TPChannel> {
                override fun onSuccess(tpChannel: TPChannel) {
                    ChannelObject.setTPChannel(tpChannel)
                    trySend(Result.Success(tpChannel))
                }
                override fun onFailure(errorCode: Int, exception: Exception) {
                    Log.d("!! : ex : ", exception.toString())
                    trySend(Result.Failure(WrappedFailResult(
                        errorCode = errorCode,
                        exception = exception
                    )))
                }
            }
        )
        awaitClose { cancel() }
    }

    fun updateChannel(
        channelName: String,
        photoFile: File?
    ): Flow<Result<TPChannel, WrappedFailResult>> = callbackFlow {
        TalkPlus.updateChannel(
            ChannelObject.tpChannel,
            ChannelObject.tpChannel.memberCount,
            false,
            channelName,
            "",
            "",
            "",
            "",
            JsonObject(),
            photoFile,
            object : TalkPlus.CallbackListener<TPChannel> {
                override fun onSuccess(tpChannel: TPChannel) {
                    ChannelObject.setTPChannel(tpChannel)
                    trySend(Result.Success(tpChannel))
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