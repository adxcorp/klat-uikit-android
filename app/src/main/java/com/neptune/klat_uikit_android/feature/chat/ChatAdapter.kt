package com.neptune.klat_uikit_android.feature.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.databinding.ItemChannelBinding
import com.neptune.klat_uikit_android.databinding.ItemChatLeftBinding
import com.neptune.klat_uikit_android.feature.chat.viewholder.LeftMessageViewHolder
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage

class ChatAdapter(
    private val tpMessages: ArrayList<TPMessage>,
    private val tpChannel: TPChannel,
    private val userId: String,
    private val onClickProfile: () -> Unit,
    private val onLongClickMessage: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemChatLeftBinding = ItemChatLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeftMessageViewHolder(
            binding = binding,
            tpChannel = tpChannel,
            onClickProfile = onClickProfile,
            onLongClickMessage = onLongClickMessage
        )
    }

    override fun getItemCount(): Int {
        return tpMessages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LeftMessageViewHolder -> holder.bind(tpMessages[position])
        }
    }

//    override fun getItemViewType(position: Int): Int {
//        val currentTPMessage: TPMessage = tpMessages[position]
//        return when {
//            (currentTPMessage.fileUrl == "") && (currentTPMessage.userId == this.userId) -> RIGHT_MESSAGE
//            (currentTPMessage.fileUrl == "") && (currentTPMessage.userId != this.userId) -> LEFT_MESSAGE
//            else -> RIGHT_IMAGE_MESSAGE
//        }
//    }

    fun addMessages(nextTpMessages: List<TPMessage>) {
        val startPosition = tpMessages.size
        tpMessages.addAll(nextTpMessages)
        notifyItemRangeInserted(startPosition, nextTpMessages.size)
    }

    companion object {
        private const val LEFT_MESSAGE = 0
        private const val LEFT_PROFILE_MESSAGE = 1
        private const val RIGHT_MESSAGE = 2
        private const val RIGHT_IMAGE_MESSAGE = 3
    }
}