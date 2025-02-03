package com.neptune.klat_uikit_android.feature.chat.viewholder

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.dpToPxInt
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.extension.loadThumbnailContainRadius
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import com.neptune.klat_uikit_android.feature.chat.reaction.ReactionAdapter
import io.talkplus.entity.channel.TPMessage
import java.text.SimpleDateFormat
import java.util.Locale

class LeftMessageViewHolder(
    private val binding: ItemChatLeftProfileBinding,
    private val onClickProfile: (TPMessage) -> Unit,
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

        Log.d("!! : current : ", currentTPMessage.text.toString())
        Log.d("!! : next : ", nextTPMessage?.text.toString())
        Log.d("!! : previous : ", previousMessage?.text.toString())

        val currentMessageCreatedTime: String = longToTime(currentTPMessage.createdAt)
        val nextMessageCreatedTime: String = longToTime(nextTPMessage?.createdAt)
        val previousMessageCreatedTime: String = longToTime(previousMessage?.createdAt)

        if (ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage) != CHAT_MESSAGES_READ_ALL) {
            tvLeftChatProfileUnReadCount.text = ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }

        ivLeftChatProfileThumbnail.setOnClickListener {
            onClickProfile.invoke(currentTPMessage)
        }

        itemView.setOnClickListener {
            if (currentTPMessage.fileUrl.isNotEmpty()) {
                ChannelObject.setTPMessage(currentTPMessage)
                onImageClick.invoke()
            }
        }

        try {
            when {
                nextMessageCreatedTime != currentMessageCreatedTime -> {
                    setTimeChangeUI(
                        currentTPMessage = currentTPMessage,
                        currentMessageCreatedTime = currentMessageCreatedTime,
                        previousMessageCreatedTime = previousMessageCreatedTime
                    )
                }

                nextMessageCreatedTime == currentMessageCreatedTime ->  {
                    setSameTimeUI(
                        currentTPMessage = currentTPMessage,
                        currentMessageCreatedTime = currentMessageCreatedTime,
                        previousMessageCreatedTime = previousMessageCreatedTime
                    )
                }

                previousMessageCreatedTime == currentMessageCreatedTime ->  {
                    setSameTimeUI(
                        currentTPMessage = currentTPMessage,
                        currentMessageCreatedTime = currentMessageCreatedTime,
                        previousMessageCreatedTime = previousMessageCreatedTime
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("!!: exception : ", e.message.toString())
        }

        itemView.setOnLongClickListener {
            onLongClickMessage.invoke(currentTPMessage, adapterPosition)
            true
        }
    }

    private fun setFirstMessageUI(
        currentTPMessage: TPMessage,
        currentMessageCreatedTime: String
    ) = with(binding) {
        setTopMargin(root, 14)
        setProfileVisibility(true)
        setProfileData(currentTPMessage.username, currentTPMessage.userProfileImage)
        setMessageTimestamp(currentMessageCreatedTime)
    }

    private fun setTimeChangeUI(
        currentMessageCreatedTime: String,
        previousMessageCreatedTime: String,
        currentTPMessage: TPMessage
    ) = with(binding) {
        if (currentMessageCreatedTime == previousMessageCreatedTime) {
            setProfileVisibility(false)
        } else {
            setProfileData(currentTPMessage.username, currentTPMessage.userProfileImage)
            setTopMargin(root, 14)
            setProfileVisibility(true)
        }
        setMessageTimestamp(currentMessageCreatedTime)
    }

    private fun setSameTimeUI(
        currentMessageCreatedTime: String,
        previousMessageCreatedTime: String,
        currentTPMessage: TPMessage
    ) = with(binding) {
        if (previousMessageCreatedTime != currentMessageCreatedTime) {
            setTopMargin(root, 14)
            setProfileVisibility(true)
            setProfileData(currentTPMessage.username, currentTPMessage.userProfileImage)
            setMessageTimestamp(null)
        } else {
            setProfileVisibility(false)
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

    private fun initView(tpMessage: TPMessage) = with(binding) {
        if (tpMessage.reactions.isEmpty()) {
            rvReactions.visibility = View.GONE
        } else {
            rvReactions.visibility = View.VISIBLE
            setReaction(tpMessage)
        }
        tvLeftChatProfileLastMessageAt.visibility = View.VISIBLE
        ivLeftChatProfileThumbnail.visibility = View.VISIBLE
        tvChatLeftProfileNickname.visibility = View.VISIBLE
        cvLeftChatProfile.visibility = View.VISIBLE
        if (tpMessage.fileUrl.isEmpty()) {
            tvLeftChatTextMessage.visibility = View.VISIBLE
            tvLeftChatTextMessage.text = tpMessage.text
        } else {
            tvLeftChatTextMessage.visibility = View.GONE
            ivLeftChatImageMessage.visibility = View.VISIBLE
            ivLeftChatImageMessage.loadThumbnailContainRadius(
                url = tpMessage.fileUrl,
                radius = 8.dpToPxInt(ivLeftChatImageMessage.context)
            )
        }
        setTopMargin(root, 0)
    }

    private fun setReaction(tpMessage: TPMessage) = with(binding) {
        rvReactions.apply {
            adapter = ReactionAdapter(tpMessage)
            layoutManager = GridLayoutManager(root.context, 4)
            itemAnimator = null
        }
    }

    private fun setProfileVisibility(isVisible: Boolean) = with(binding) {
        cvLeftChatProfile.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        tvChatLeftProfileNickname.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setProfileData(
        username: String,
        imageUrl: String
    ) = with(binding) {
        tvChatLeftProfileNickname.text = username
        ivLeftChatProfileThumbnail.loadThumbnail(imageUrl)
    }

    private fun setMessageTimestamp(timestamp: String?) = with(binding) {
        tvLeftChatProfileLastMessageAt.text = timestamp
        tvLeftChatProfileLastMessageAt.visibility = if (timestamp != null) View.VISIBLE else View.GONE
    }

    companion object {
        const val CHAT_MESSAGES_READ_ALL = 0
        private const val INVALID_TIME = "-1"
        private const val FIRST_INDEX = 0
    }
}