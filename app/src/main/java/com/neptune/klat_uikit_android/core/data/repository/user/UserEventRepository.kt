package com.neptune.klat_uikit_android.core.data.repository.user

import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMember
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class UserEventRepository {
    companion object {
        private const val NO_LIMIT = 0
    }

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

    fun muteUser(targetId: String) = callbackFlow {
        TalkPlus.muteMemberToChannel(
            ChannelObject.tpChannel,
            targetId,
            NO_LIMIT,
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

    fun unMuteUser(targetId: String) = callbackFlow {
        TalkPlus.unMuteMemberToChannel(
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
            }
        )
        awaitClose { cancel() }
    }

    fun peerMuteUser(targetId: String) = callbackFlow {
        TalkPlus.mutePeerToChannel(
            ChannelObject.tpChannel,
            targetId,
            NO_LIMIT,
            object : TalkPlus.TPCallbackListener<TPChannel, List<TPMember>> {
                override fun onSuccess(tpChannel: TPChannel, peerMutedMemebers: List<TPMember>) {
                    ChannelObject.setTPChannel(tpChannel)
                    trySend(Result.Success(peerMutedMemebers))
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

    fun peerUnMuteUser(targetId: String) = callbackFlow {
        TalkPlus.unMutePeerToChannel(
            ChannelObject.tpChannel,
            targetId,
            object : TalkPlus.TPCallbackListener<TPChannel, List<TPMember>> {
                override fun onSuccess(tpChannel: TPChannel, peerMutedMemebers: List<TPMember>) {
                    ChannelObject.setTPChannel(tpChannel)
                    trySend(Result.Success(peerMutedMemebers))
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