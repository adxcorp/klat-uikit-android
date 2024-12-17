package com.neptune.klat_uikit_android.core.data.repository.chat

import com.neptune.klat_uikit_android.core.data.model.MessagesResponse
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult
import io.talkplus.TalkPlus
import io.talkplus.TalkPlus.TPCallbackListener
import io.talkplus.entity.channel.TPMessage
import io.talkplus.params.TPMessageRetrievalParams
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
}