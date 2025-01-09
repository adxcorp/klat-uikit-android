package com.neptune.klat_uikit_android.feature.chat.viewholder

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.extension.dpToPx
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import org.jetbrains.annotations.TestOnly
import java.text.SimpleDateFormat
import java.util.Locale

class LeftMessageViewHolder(
    private val binding: ItemChatLeftProfileBinding,
    private val onClickProfile: (TPMessage) -> Unit,
    private val onLongClickMessage: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        currentTPMessage: TPMessage,
        previousTPMessage: TPMessage?,
        nextTPMessage: TPMessage?,
        tpMessages: List<TPMessage>
    ) = with(binding) {

        val previousMessageCreatedTime: String = longToTime(previousTPMessage?.createdAt)
        val currentMessageCreatedTime: String = longToTime(currentTPMessage.createdAt)
        val nextMessageCreatedTime: String = longToTime(nextTPMessage?.createdAt)

        tvLeftChatProfileMessage.text = currentTPMessage.text

        if (tpChannel.getMessageUnreadCount(currentTPMessage) != CHAT_MESSAGES_READ_ALL) {
            tvLeftChatProfileUnReadCount.text = tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }

        ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)

        ivLeftChatProfileThumbnail.setOnClickListener {
            onClickProfile.invoke(currentTPMessage)
        }

        tvLeftChatProfileLastMessageAt.text = currentMessageCreatedTime


        // 테스트
        itemView.setOnClickListener {
            TalkPlus.addMessageReaction(currentTPMessage,
                "happy???zzz",
                object : TalkPlus.CallbackListener<TPMessage> {
                    override fun onSuccess(tpMessage: TPMessage) {
                        Log.d("!! : 성공 : ", tpMessage.toString())
                    }
                    override fun onFailure(errorCode: Int, exception: Exception) {

                    }
                })
        }
    }

    private fun longToTime(createdAt: Long?): String {
        return createdAt?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(it) } ?: INVALID_TIME
    }

    companion object {
        const val CHAT_MESSAGES_READ_ALL = 0
        private const val INVALID_TIME = "-1"
        private const val FIRST_INDEX = 0
    }
}