package com.neptune.klat_uikit_android.feature.chat.viewholder

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.extension.dpToPx
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import java.text.SimpleDateFormat
import java.util.Locale

class LeftMessageViewHolder(
    private val binding: ItemChatLeftProfileBinding,
    private val tpChannel: TPChannel,
    private val context: Context,
    onClickProfile: () -> Unit,
    onLongClickMessage: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        currentTPMessage: TPMessage,
        previousTPMessage: TPMessage,
        nextTPMessage: TPMessage,
        tpMessages: List<TPMessage>
    ) = with(binding) {

        val previousMessageCreatedTime: String = longToTime(previousTPMessage.createdAt)
        val currentMessageCreatedTime: String = longToTime(currentTPMessage.createdAt)
        val nextMessageCreatedTime: String = longToTime(nextTPMessage.createdAt)

        Log.d("!! --------------------", "-----------------------")
        Log.d("!! ${adapterPosition + 1}번째 prv : ", previousTPMessage.text + " " + previousMessageCreatedTime)
        Log.d("!! ${adapterPosition + 1}번째 current : ", currentTPMessage.text + " " + currentMessageCreatedTime)
        Log.d("!! ${adapterPosition + 1}번째 next : ", nextTPMessage.text + " " + nextMessageCreatedTime)

        tvLeftChatProfileMessage.text = currentTPMessage.text

        when {
            tpMessages.size == 1 -> {
                clItemChatLeftRoot.setPadding(0, 12.dpToPx(context).toInt(), 0, 0)
                ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)
                tvChatLeftProfileNickname.text = currentTPMessage.username
            }

            adapterPosition == 0 -> {
                when (currentMessageCreatedTime == nextMessageCreatedTime) {
                    true -> {
                        cvLeftChatProfile.visibility = View.INVISIBLE
                        tvChatLeftProfileNickname.visibility = View.GONE
                        tvLeftChatProfileLastMessageAt.text = currentMessageCreatedTime
                    }

                    false -> {
                        clItemChatLeftRoot.setPadding(0, 12.dpToPx(context).toInt(), 0, 0)
                        ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)
                        tvChatLeftProfileNickname.text = currentTPMessage.username
                        tvLeftChatProfileLastMessageAt.text = currentMessageCreatedTime
                    }
                }
            }

            currentMessageCreatedTime == previousMessageCreatedTime && currentMessageCreatedTime != nextMessageCreatedTime -> {
                clItemChatLeftRoot.setPadding(0, 12.dpToPx(context).toInt(), 0, 0)
                ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)
                tvChatLeftProfileNickname.text = currentTPMessage.username
                tvLeftChatProfileLastMessageAt.visibility = View.GONE
                Log.d("!! : 하트1 : ", currentTPMessage.text)
            }

            currentMessageCreatedTime == nextMessageCreatedTime && currentMessageCreatedTime == previousMessageCreatedTime -> {
                cvLeftChatProfile.visibility = View.INVISIBLE
                tvChatLeftProfileNickname.visibility = View.GONE
                tvLeftChatProfileLastMessageAt.visibility = View.GONE
                Log.d("!! : 하트2 : ", currentTPMessage.text)
            }

            currentMessageCreatedTime == nextMessageCreatedTime && currentMessageCreatedTime != previousMessageCreatedTime -> {
                Log.d("!! : 하트3 : ", currentTPMessage.text)
                if (adapterPosition == tpMessages.lastIndex) {
                    Log.d("!! : 하트3 마지막 : ", currentTPMessage.text)
                    clItemChatLeftRoot.setPadding(0, 12.dpToPx(context).toInt(), 0, 0)
                    ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)
                    tvChatLeftProfileNickname.text = currentTPMessage.username
                    tvLeftChatProfileLastMessageAt.text = currentMessageCreatedTime
                } else {
                    cvLeftChatProfile.visibility = View.INVISIBLE
                    tvChatLeftProfileNickname.visibility = View.GONE
                    tvLeftChatProfileLastMessageAt.text = currentMessageCreatedTime
                }
            }

            currentMessageCreatedTime != nextMessageCreatedTime && currentMessageCreatedTime != previousMessageCreatedTime -> {
                Log.d("!! : 하트4 : ", currentTPMessage.text)
                clItemChatLeftRoot.setPadding(0, 12.dpToPx(context).toInt(), 0, 0)
                ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)
                tvChatLeftProfileNickname.text = currentTPMessage.username
                tvLeftChatProfileLastMessageAt.text = currentMessageCreatedTime
            }
        }

        if (tpChannel.getMessageUnreadCount(currentTPMessage) != CHAT_MESSAGES_READ_ALL) {
            tvLeftChatProfileUnReadCount.text = tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }
    }

    private fun longToTime(createdAt: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(createdAt)
    }

    companion object {
        private const val CHAT_MESSAGES_READ_ALL = 0
        private const val FIRST_INDEX = 0
    }
}