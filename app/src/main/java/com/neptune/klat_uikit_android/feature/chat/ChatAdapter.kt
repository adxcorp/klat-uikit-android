package com.neptune.klat_uikit_android.feature.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import com.neptune.klat_uikit_android.databinding.ItemChatRightBinding
import com.neptune.klat_uikit_android.feature.chat.viewholder.LeftMessageViewHolder
import com.neptune.klat_uikit_android.feature.chat.viewholder.RightMessageViewHolder
import io.talkplus.entity.channel.TPMessage

class ChatAdapter(
    private val tpMessages: ArrayList<TPMessage> = arrayListOf(),
    private val onClickProfile: (TPMessage) -> Unit,
    private val onLongClickMessage: (TPMessage, Int) -> Unit,
    private val onImageClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LEFT_MESSAGE -> showLeftMessage(parent)
            RIGHT_MESSAGE -> showRightMessage(parent)
            else -> error("")
        }
    }

    override fun getItemCount(): Int {
        return tpMessages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LeftMessageViewHolder -> holder.bind(
                currentTPMessage = tpMessages[position],
                nextTPMessage = tpMessages.getOrNull(position+1),
                previousMessage = tpMessages.getOrNull(position-1),
                tpMessages = tpMessages
            )

            is RightMessageViewHolder -> holder.bind(
                currentTPMessage = tpMessages[position],
                nextTPMessage = tpMessages.getOrNull(position+1),
                previousMessage = tpMessages.getOrNull(position-1),
                tpMessages = tpMessages
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentTPMessage: TPMessage = tpMessages[position]
        return when {
            (currentTPMessage.userId == ChannelObject.userId) -> RIGHT_MESSAGE
            (currentTPMessage.userId != ChannelObject.userId) -> LEFT_MESSAGE
            else -> error("")
        }
    }

    private fun showLeftMessage(parent: ViewGroup): LeftMessageViewHolder {
        val binding = ItemChatLeftProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeftMessageViewHolder(
            binding = binding,
            onClickProfile = onClickProfile,
            onLongClickMessage = onLongClickMessage,
            onImageClick = onImageClick
        )
    }

    private fun showRightMessage(parent: ViewGroup): RightMessageViewHolder {
        val binding = ItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RightMessageViewHolder(
            binding = binding,
//            onClickProfile = onClickProfile,
//            onLongClickMessage = onLongClickMessage,
//            onImageClick = onImageClick
        )
    }

    fun addMessages(nextTpMessages: List<TPMessage>) {
        tpMessages.addAll(FIRST_POSITION, nextTpMessages)
        notifyItemRangeInserted(FIRST_POSITION, nextTpMessages.size)
        updateLastMessage()
    }

    fun addMessage(newTPMessage: TPMessage) {
        tpMessages.add(newTPMessage)
        notifyItemInserted(tpMessages.size)
        updatePreviousMessage()
    }

    private fun updatePreviousMessage() {
        if (tpMessages.size != ONLY_ONE) {
            val previousPosition = tpMessages.size - 2
            notifyItemChanged(previousPosition)
        }
    }

    fun updateReaction(position: Int, tpMessage: TPMessage) {
        tpMessages[position] = tpMessage
        notifyItemChanged(position)
    }

    private fun updateLastMessage() {
        notifyItemChanged(LAST_POSITION)
    }

    companion object {
        private const val LEFT_MESSAGE = 0
        private const val RIGHT_MESSAGE = 2

        private const val LAST_POSITION = 19
        private const val FIRST_POSITION = 0

        private const val ONLY_ONE = 1
    }
}