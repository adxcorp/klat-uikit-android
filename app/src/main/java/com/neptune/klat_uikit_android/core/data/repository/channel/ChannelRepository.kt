package com.neptune.klat_uikit_android.core.data.repository.channel

import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class ChannelRepository {
    fun getChannelList(lastChannel: TPChannel?): Flow<Result<Pair<List<TPChannel>, Boolean>, WrappedFailResult>> {
        return callbackFlow {
            TalkPlus.getChannels(lastChannel, object : TalkPlus.TPCallbackListener<List<TPChannel>, Boolean> {
                override fun onSuccess(tpChannelList: List<TPChannel>, hasNext: Boolean) {
                    trySend(Result.Success(tpChannelList to hasNext))
                }

                override fun onFailure(errorCode: Int, exception: Exception) {
                    trySend(Result.Failure(WrappedFailResult(errorCode = errorCode, exception = exception)))
                }
            })
            awaitClose { cancel() }
        }
    }
}