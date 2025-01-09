package com.neptune.klat_uikit_android.feature.chat.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.databinding.ItemChatRightBinding
import io.talkplus.entity.channel.TPMessage

class RightMessageViewHolder(private val binding: ItemChatRightBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(currentTPMessage: TPMessage) = with(binding) {
        if (ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage) != LeftMessageViewHolder.CHAT_MESSAGES_READ_ALL) {
            tvChatRightUnReadCount.text = ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }

        tvChatRightMessage.text = currentTPMessage.text
    }
}