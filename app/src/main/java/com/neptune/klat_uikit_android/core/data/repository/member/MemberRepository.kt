package com.neptune.klat_uikit_android.core.data.repository.member

import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult
import com.neptune.klat_uikit_android.core.data.model.member.MemberResponse
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMember
import io.talkplus.entity.user.TPUser
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class MemberRepository {
    fun getMutedMembers(
        tpChannel: TPChannel,
        lastUser: TPUser?
    ): Flow<Result<MemberResponse, WrappedFailResult>> {
        return callbackFlow {
            TalkPlus.getMutedChannelMembers(lastUser as TPMember?, tpChannel, object : TalkPlus.TPCallbackListener<List<TPMember>, Boolean> {
                override fun onSuccess(tpMembers: List<TPMember>, hasNext: Boolean) {
                    trySend(Result.Success(MemberResponse(
                        tpMembers = tpMembers as List<TPUser>,
                        hasNext = hasNext
                    )))
                }

                override fun onFailure(errorCode: Int, exception: Exception) {
                    trySend(
                        Result.Failure(WrappedFailResult(
                        errorCode = errorCode,
                        exception = exception
                    )))
                }
            })
            awaitClose { cancel() }
        }
    }

    fun getPeerMutedMembers(
        tpChannel: TPChannel,
        lastUser: TPUser?
    ) = callbackFlow {
        TalkPlus.getMutedPeers(tpChannel, lastUser as TPMember?, object : TalkPlus.TPCallbackListener<List<TPMember>, Boolean> {
            override fun onSuccess(tpMembers: List<TPMember>, hasNext: Boolean) {
                trySend(Result.Success(MemberResponse(
                    tpMembers = tpMembers,
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

    fun getMembers(
        tpChannel: TPChannel,
        lastUser: TPUser?
    ): Flow<Result<MemberResponse, WrappedFailResult>> {
        return callbackFlow {
            TalkPlus.getChannelMembers(tpChannel, lastUser as TPMember?, object : TalkPlus.TPCallbackListener<List<TPMember>, Boolean> {
                override fun onSuccess(tpMembers: List<TPMember>, hasNext: Boolean) {
                    trySend(Result.Success(MemberResponse(
                        tpMembers = tpMembers as List<TPUser>,
                        hasNext = hasNext
                    )))
                }

                override fun onFailure(errorCode: Int, exception: Exception) {
                    trySend(
                        Result.Failure(WrappedFailResult(
                            errorCode = errorCode,
                            exception = exception
                        )))
                }
            })
            awaitClose { cancel() }
        }
    }
}