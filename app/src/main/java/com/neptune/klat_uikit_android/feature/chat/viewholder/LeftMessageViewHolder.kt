package com.neptune.klat_uikit_android.feature.chat.viewholder

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.dpToPx
import com.neptune.klat_uikit_android.core.extension.dpToPxInt
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import io.talkplus.TalkPlus
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

        tvLeftChatProfileLastMessageAt.visibility = View.VISIBLE
        ivLeftChatProfileThumbnail.visibility = View.VISIBLE
        tvChatLeftProfileNickname.visibility = View.VISIBLE
        cvLeftChatProfile.visibility = View.VISIBLE

        topMargin(binding.root, 0)

        val currentMessageCreatedTime: String = longToTime(currentTPMessage.createdAt)
        val nextMessageCreatedTime: String = longToTime(nextTPMessage?.createdAt)
        val previousMessageCreatedTime: String = longToTime(previousMessage?.createdAt)

        tvLeftChatProfileMessage.text = currentTPMessage.text

        if (ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage) != CHAT_MESSAGES_READ_ALL) {
            tvLeftChatProfileUnReadCount.text = ChannelObject.tpChannel.getMessageUnreadCount(currentTPMessage).toString()
        }

        ivLeftChatProfileThumbnail.setOnClickListener {
            onClickProfile.invoke(currentTPMessage)
        }

        when {
            nextMessageCreatedTime == INVALID_TIME -> {
                topMargin(root, 14)
                cvLeftChatProfile.visibility = View.VISIBLE
                tvChatLeftProfileNickname.text = currentTPMessage.username
                ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)
                tvLeftChatProfileLastMessageAt.text = currentMessageCreatedTime
            }
            nextMessageCreatedTime != currentMessageCreatedTime ->  {
                if (currentMessageCreatedTime == previousMessageCreatedTime) {
                    cvLeftChatProfile.visibility = View.INVISIBLE
                    tvChatLeftProfileNickname.visibility = View.GONE
                } else {
                    ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)
                    tvChatLeftProfileNickname.text = currentTPMessage.username
                    topMargin(root, 14)
                    cvLeftChatProfile.visibility = View.VISIBLE
                }
                tvLeftChatProfileLastMessageAt.text = currentMessageCreatedTime
            }
            nextMessageCreatedTime == currentMessageCreatedTime ->  {
                when (previousMessageCreatedTime != currentMessageCreatedTime) {
                    true -> {
                        topMargin(root, 14)
                        cvLeftChatProfile.visibility = View.VISIBLE
                        ivLeftChatProfileThumbnail.loadThumbnail(currentTPMessage.userProfileImage)
                        tvChatLeftProfileNickname.visibility = View.VISIBLE
                        tvChatLeftProfileNickname.text = currentTPMessage.username
                        tvLeftChatProfileLastMessageAt.visibility = View.GONE
                    }
                    false -> {
                        cvLeftChatProfile.visibility = View.INVISIBLE
                        tvChatLeftProfileNickname.visibility = View.GONE
                        tvLeftChatProfileLastMessageAt.visibility = View.GONE
                    }
                }
            }
        }

        // 리액션 테스트
        itemView.setOnClickListener {
            TalkPlus.addMessageReaction(currentTPMessage,
                "❤️",
                object : TalkPlus.CallbackListener<TPMessage> {
                    override fun onSuccess(tpMessage: TPMessage) {
                        Log.d("!! : 성공 : ", tpMessage.toString())
                    }
                    override fun onFailure(errorCode: Int, exception: Exception) {
                        Log.d("!! : 실패 : ", exception.toString())
                    }
                })
        }
    }

    private fun longToTime(createdAt: Long?): String {
        return createdAt?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(it) } ?: INVALID_TIME
    }

    private fun topMargin(
        rootView: ConstraintLayout,
        margin: Int
    ) {
        val layoutParams = rootView.layoutParams as? ViewGroup.MarginLayoutParams
        layoutParams?.topMargin = margin.dpToPxInt(itemView.context)
    }

    companion object {
        const val CHAT_MESSAGES_READ_ALL = 0
        private const val INVALID_TIME = "-1"
        private const val FIRST_INDEX = 0
    }
}