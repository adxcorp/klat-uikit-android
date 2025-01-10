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
    private val tpMessages: ArrayList<TPMessage>,
    private val onClickProfile: (TPMessage) -> Unit,
    private val onLongClickMessage: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LEFT_MESSAGE -> showLeftMessage(parent)
            RIGHT_MESSAGE -> {
                val binding: ItemChatRightBinding = ItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RightMessageViewHolder(binding = binding)
            }

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
                previousTPMessage = tpMessages.getOrNull(position-1)
            )

            is RightMessageViewHolder -> holder.bind(
                currentTPMessage = tpMessages[position]
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
        )
    }

    fun addMessages(nextTpMessages: List<TPMessage>) {
        tpMessages.addAll(0, nextTpMessages)
        notifyItemRangeInserted(0, nextTpMessages.size)
    }

    fun addMessage(newTPMessage: TPMessage) {
        tpMessages.add(newTPMessage)
        notifyItemInserted(tpMessages.size-1)
    }

    companion object {
        private const val LEFT_MESSAGE = 0
        private const val LEFT_PROFILE_MESSAGE = 1
        private const val RIGHT_MESSAGE = 2
        private const val RIGHT_IMAGE_MESSAGE = 3
    }
}