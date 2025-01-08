package com.neptune.klat_uikit_android.core.data.repository.user

import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class UserEventRepository {
    fun banUser(targetId: String): Flow<Result<TPChannel, WrappedFailResult>> = callbackFlow {
        TalkPlus.banMemberToChannel(
            ChannelObject.tpChannel,
            targetId,
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
        })
        awaitClose { cancel() }
    }

    fun grantOwner(targetId: String): Flow<Result<TPChannel, WrappedFailResult>> = callbackFlow {
        TalkPlus.transferChannelOwnership(
            ChannelObject.tpChannel,
            targetId,
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
            })
        awaitClose { cancel() }
    }
}