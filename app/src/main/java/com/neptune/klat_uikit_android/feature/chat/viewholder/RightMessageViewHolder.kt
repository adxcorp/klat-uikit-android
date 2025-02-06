package com.neptune.klat_uikit_android.feature.chat.viewholder

import android.content.Context
import android.graphics.Rect
import android.util.LayoutDirection
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.dpToPx
import com.neptune.klat_uikit_android.core.extension.dpToPxInt
import com.neptune.klat_uikit_android.core.extension.loadThumbnailContainRadius
import com.neptune.klat_uikit_android.databinding.ItemChatRightBinding
import com.neptune.klat_uikit_android.feature.chat.reaction.ReactionAdapter
import io.talkplus.entity.channel.TPMessage
import java.text.SimpleDateFormat
import java.util.Locale

class RightMessageViewHolder(
    private val binding: ItemChatRightBinding,
    private val onLongClickMessage: (TPMessage, Int) -> Unit,
    private val onImageClick: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        currentTPMessage: TPMessage,
        nextTPMessage: TPMessage?,
        previousMessage: TPMessage?,
        tpMessages: List<TPMessage>
    ) = with(binding) {
        initView(currentTPMessage)

        val currentMessageCreatedTime: String = longToTime(currentTPMessage.createdAt)
        val nextMessageCreatedTime: String = longToTime(nextTPMessage?.createdAt)
        val previousMessageCreatedTime: String = longToTime(previousMessage?.createdAt)

        if (ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage) != LeftMessageViewHolder.CHAT_MESSAGES_READ_ALL) {
            tvChatRightUnReadCount.text = ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }

        try {
            when {
                nextMessageCreatedTime != currentMessageCreatedTime -> {
                    setTimeChangeUI(
                        currentMessageCreatedTime = currentMessageCreatedTime,
                        previousMessageCreatedTime = previousMessageCreatedTime
                    )
                }
                previousMessageCreatedTime == currentMessageCreatedTime -> setSameTimeUI()
                nextMessageCreatedTime == currentMessageCreatedTime -> setSameTimeUI()
            }
        } catch (e: Exception) {

        }

        itemView.setOnLongClickListener {
            onLongClickMessage.invoke(currentTPMessage, adapterPosition)
            true
        }

        itemView.setOnClickListener {
            if (currentTPMessage.fileUrl.isNotEmpty()) {
                ChannelObject.setTPMessage(currentTPMessage)
                onImageClick.invoke()
            }
        }
    }

    fun updateUnreadCount(currentTPMessage: TPMessage) = with(binding) {
        Log.d("!! : message : ", currentTPMessage.text.toString())
        Log.d("!! : count : ", ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage).toString())
//        if (ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage).minus(1) <= 0) {
//            tvChatRightUnReadCount.text = ""
//            return@with
//        }
        val unreadCount = ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage)
        tvChatRightUnReadCount.text = if (unreadCount == 0) "" else unreadCount.toString()

    }

    private fun initView(tpMessage: TPMessage) = with(binding) {
        tvChatRightLastMessageAt.visibility = View.VISIBLE
        ivRightChatImageMessage.visibility = View.GONE

        if (tpMessage.reactions.isEmpty()) {
            rvRightReactions.visibility = View.GONE
        } else {
            rvRightReactions.visibility = View.VISIBLE
            setReaction(tpMessage)
        }

        if (tpMessage.fileUrl.isEmpty()) {
            tvChatRightMessage.visibility = View.VISIBLE
            tvChatRightMessage.text = tpMessage.text
        } else {
            tvChatRightMessage.visibility = View.GONE
            ivRightChatImageMessage.visibility = View.VISIBLE
            ivRightChatImageMessage.loadThumbnailContainRadius(
                url = tpMessage.fileUrl,
                radius = 8.dpToPxInt(ivRightChatImageMessage.context)
            )
        }
        setTopMargin(root, 0)
    }

    private fun setReaction(tpMessage: TPMessage) = with(binding) {
        rvRightReactions.apply {
            layoutDirection = View.LAYOUT_DIRECTION_RTL
            setHasFixedSize(true)
            adapter = ReactionAdapter(tpMessage = tpMessage)
            layoutManager = GridLayoutManager(root.context, 4)
            itemAnimator = null
        }
    }

    private fun setTimeChangeUI(
        currentMessageCreatedTime: String,
        previousMessageCreatedTime: String
    ) = with(binding) {
        setMessageTimestamp(currentMessageCreatedTime)
        if (currentMessageCreatedTime != previousMessageCreatedTime) {
            setTopMargin(root, 14)
        }
    }

    private fun setSameTimeUI() = with(binding) {
        setMessageTimestamp(null)
    }

    private fun longToTime(createdAt: Long?): String {
        return createdAt?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(it) } ?: INVALID_TIME
    }

    private fun setTopMargin(
        rootView: ConstraintLayout,
        margin: Int
    ) {
        val layoutParams = rootView.layoutParams as? ViewGroup.MarginLayoutParams
        layoutParams?.topMargin = margin.dpToPxInt(itemView.context)
    }

    private fun setMessageTimestamp(timestamp: String?) = with(binding) {
        tvChatRightLastMessageAt.text = timestamp
        tvChatRightLastMessageAt.visibility = if (timestamp != null) View.VISIBLE else View.GONE
    }

    companion object {
        private const val INVALID_TIME = "-1"
    }
}