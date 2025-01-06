package com.neptune.klat_uikit_android.feature.chat.viewholder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import com.neptune.klat_uikit_android.databinding.ItemChatRightBinding
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage

class RightMessageViewHolder(
    private val binding: ItemChatRightBinding,
    private val tpChannel: TPChannel,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(currentTPMessage: TPMessage) = with(binding) {
        if (tpChannel.getMessageUnreadCount(currentTPMessage) != LeftMessageViewHolder.CHAT_MESSAGES_READ_ALL) {
            tvChatRightUnReadCount.text = tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }

        tvChatRightMessage.text = currentTPMessage.text
    }
}