package com.neptune.klat_uikit_android.feature.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.databinding.ItemChannelBinding
import com.neptune.klat_uikit_android.databinding.ItemChatLeftBinding
import com.neptune.klat_uikit_android.databinding.ItemChatLeftProfileBinding
import com.neptune.klat_uikit_android.feature.chat.viewholder.LeftMessageViewHolder
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import java.lang.Exception

class ChatAdapter(
    private val tpMessages: ArrayList<TPMessage>,
    private val tpChannel: TPChannel,
    private val userId: String,
    private val context: Context,
    private val onClickProfile: () -> Unit,
    private val onLongClickMessage: () -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemChatLeftProfileBinding = ItemChatLeftProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeftMessageViewHolder(
            binding = binding,
            tpChannel = tpChannel,
            onClickProfile = onClickProfile,
            onLongClickMessage = onLongClickMessage,
            context = context
        )
    }

    override fun getItemCount(): Int {
        val numbers = listOf(1, 2, 3)
        numbers.find { it == 3 }
        return tpMessages.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LeftMessageViewHolder -> holder.bind(
                currentTPMessage = tpMessages[position],
                previousTPMessage = try { tpMessages[position-1] } catch (e: Exception) { tpMessages[position] },
                nextTPMessage = try { tpMessages[position+1] } catch (e: Exception) { tpMessages[position] },
                tpMessages
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
//        val currentTPMessage: TPMessage = tpMessages[position]
//        return when {
//            (currentTPMessage.fileUrl == "") && (currentTPMessage.userId == this.userId) -> RIGHT_MESSAGE
//            (currentTPMessage.fileUrl == "") && (currentTPMessage.userId != this.userId) -> LEFT_MESSAGE
//            else -> RIGHT_IMAGE_MESSAGE
//        }
    }

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