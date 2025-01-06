package com.neptune.klat_uikit_android.feature.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.databinding.ItemChannelBinding
import com.neptune.klat_uikit_android.databinding.ItemChatLeftBinding
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import com.neptune.klat_uikit_android.databinding.ItemChatRightBinding
import com.neptune.klat_uikit_android.feature.chat.viewholder.LeftMessageViewHolder
import com.neptune.klat_uikit_android.feature.chat.viewholder.RightMessageViewHolder
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import java.lang.Exception

class ChatAdapter(
    private val tpMessages: ArrayList<TPMessage>,
    private val tpChannel: TPChannel,
    private val userId: String,
    private val context: Context,
    private val onClickProfile: (TPMessage, String, String) -> Unit,
    private val onLongClickMessage: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LEFT_MESSAGE -> {
                val binding: ItemChatLeftProfileBinding = ItemChatLeftProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LeftMessageViewHolder(
                    binding = binding,
                    tpChannel = tpChannel,
                    onClickProfile = onClickProfile,
                    onLongClickMessage = onLongClickMessage,
                    userId = userId,
                    context = context
                )
            }

            RIGHT_MESSAGE -> {
                val binding: ItemChatRightBinding = ItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                RightMessageViewHolder(
                    binding = binding,
                    tpChannel = tpChannel,
                )
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
                previousTPMessage = tpMessages.getOrNull(position-1),
                nextTPMessage = tpMessages.getOrNull(position+1),
                tpMessages
            )

            is RightMessageViewHolder -> holder.bind(
                currentTPMessage = tpMessages[position]
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentTPMessage: TPMessage = tpMessages[position]
        return when {
            (currentTPMessage.userId == this.userId) -> RIGHT_MESSAGE
            (currentTPMessage.userId != this.userId) -> LEFT_MESSAGE
            else -> error("")
        }
    }

    fun addMessages(nextTpMessages: List<TPMessage>) {
        val startPosition = tpMessages.size
        tpMessages.addAll(nextTpMessages)
        notifyItemRangeInserted(startPosition, nextTpMessages.size)
    }

    fun addMessage(newTPMessage: TPMessage) {
        tpMessages.add(0, newTPMessage)
        notifyItemInserted(0)
    }

    companion object {
        private const val LEFT_MESSAGE = 0
        private const val LEFT_PROFILE_MESSAGE = 1
        private const val RIGHT_MESSAGE = 2
        private const val RIGHT_IMAGE_MESSAGE = 3
    }
}