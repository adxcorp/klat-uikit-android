package com.neptune.klat_uikit_android.feature.chat.viewholder

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.databinding.ItemChatLeftBinding
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import java.text.SimpleDateFormat
import java.util.Locale

class LeftMessageViewHolder(
    private val binding: ItemChatLeftBinding,
    private val tpChannel: TPChannel,
    onClickProfile: () -> Unit,
    onLongClickMessage: () -> Unit
): RecyclerView.ViewHolder(binding.root) {
    fun bind(
        currentTPMessage: TPMessage,
        previousTPMessage: TPMessage
    ) = with(binding) {
        tvChatLeftMessage.text = currentTPMessage.text

        if (adapterPosition == 0) {
            tvChatLeftLastMessageAt.text = longToTime(currentTPMessage.createdAt)
        } else {
            val previousMessageCreatedTime: String = longToTime(previousTPMessage.createdAt)
            val currentMessageCreatedTime: String = longToTime(currentTPMessage.createdAt)

            when {
                currentMessageCreatedTime == previousMessageCreatedTime -> tvChatLeftLastMessageAt.visibility = View.GONE
                currentMessageCreatedTime != previousMessageCreatedTime -> tvChatLeftLastMessageAt.text = currentMessageCreatedTime
            }
        }

        if (tpChannel.getMessageUnreadCount(currentTPMessage) != CHAT_MESSAGES_READ_ALL) {
            tvChatLeftUnReadCount.text =tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }
    }

    private fun longToTime(createdAt: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(createdAt)
    }

    companion object {
        private const val CHAT_MESSAGES_READ_ALL = 0
    }
}