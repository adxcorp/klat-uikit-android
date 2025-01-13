package com.neptune.klat_uikit_android.feature.chat.viewholder

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.dpToPxInt
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import io.talkplus.entity.channel.TPMessage
import java.text.SimpleDateFormat
import java.util.Locale

class LeftMessageViewHolder(
    private val binding: ItemChatLeftProfileBinding,
    private val onClickProfile: (TPMessage) -> Unit,
    private val onLongClickMessage: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        currentTPMessage: TPMessage,
        nextTPMessage: TPMessage?,
        previousMessage: TPMessage?
    ) = with(binding) {
        initView(currentTPMessage)

        val currentMessageCreatedTime: String = longToTime(currentTPMessage.createdAt)
        val nextMessageCreatedTime: String = longToTime(nextTPMessage?.createdAt)
        val previousMessageCreatedTime: String = longToTime(previousMessage?.createdAt)

        if (ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage) != CHAT_MESSAGES_READ_ALL) {
            tvLeftChatProfileUnReadCount.text = ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }

        ivLeftChatProfileThumbnail.setOnClickListener {
            onClickProfile.invoke(currentTPMessage)
        }

        when {
            nextMessageCreatedTime == INVALID_TIME -> {
                setFirstMessageUI(
                    currentTPMessage = currentTPMessage,
                    currentMessageCreatedTime = currentMessageCreatedTime
                )
            }

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
        }

        itemView.setOnClickListener {

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
        tvLeftChatProfileLastMessageAt.visibility = View.VISIBLE
        ivLeftChatProfileThumbnail.visibility = View.VISIBLE
        tvChatLeftProfileNickname.visibility = View.VISIBLE
        cvLeftChatProfile.visibility = View.VISIBLE
        tvLeftChatProfileMessage.text = tpMessage.text
        setTopMargin(root, 0)
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