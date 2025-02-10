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

        if (previousMessage?.userId != currentTPMessage.userId && nextTPMessage?.userId != currentTPMessage.userId) {
            setTimeChangeUI(
                currentMessageCreatedTime = currentMessageCreatedTime,
                previousMessageCreatedTime = previousMessageCreatedTime
            )
        } else {
            when {
                nextMessageCreatedTime != currentMessageCreatedTime -> {
                    setTimeChangeUI(
                        currentMessageCreatedTime = currentMessageCreatedTime,
                        previousMessageCreatedTime = previousMessageCreatedTime
                    )
                }
                previousMessageCreatedTime == currentMessageCreatedTime ->  {
                    setSameTimeUI(
                        currentTPMessage, nextTPMessage, currentMessageCreatedTime
                    )
                }
                nextMessageCreatedTime == currentMessageCreatedTime ->  {
                    setSameTimeUI(currentTPMessage, nextTPMessage, currentMessageCreatedTime)
                }
            }
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
            adapter = ReactionAdapter(tpMessage = tpMessage)
            layoutManager = RightToLeftGridLayoutManager(
                root.context,
                spanCount = 4,
                orientation = GridLayoutManager.VERTICAL
            )
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

    private fun setSameTimeUI(
        currentTPMessage: TPMessage,
        nextTPMessage: TPMessage?,
        currentMessageCreatedTime: String
    ) = with(binding) {
        if (currentTPMessage.userId != nextTPMessage?.userId) {
            setMessageTimestamp(currentMessageCreatedTime)
        } else {
            setMessageTimestamp(null)
        }
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

class RightToLeftGridLayoutManager(
    context: Context?,
    spanCount: Int,
    orientation: Int
) : GridLayoutManager(
    context,
    spanCount,
    orientation,
    false
) {
    override fun layoutDecoratedWithMargins(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        val parentWidth = width
        val newLeft = parentWidth - right
        val newRight = parentWidth - left
        super.layoutDecoratedWithMargins(child, newLeft, top, newRight, bottom)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
    }
}