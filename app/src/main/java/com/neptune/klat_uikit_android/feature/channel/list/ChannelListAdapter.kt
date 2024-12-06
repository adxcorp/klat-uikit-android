package com.neptune.klat_uikit_android.feature.channel.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ItemChannelBinding
import io.talkplus.entity.channel.TPChannel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChannelListAdapter(
    private val channelList: ArrayList<TPChannel>,
    private val onClick: (TPChannel) -> Unit
) : RecyclerView.Adapter<ChannelListAdapter.ChannelViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding: ItemChannelBinding = ItemChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return channelList.size
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(channelList[position])
    }

    inner class ChannelViewHolder(private val binding: ItemChannelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tpChannel: TPChannel) = with(binding) {
            tvChannelName.text = tpChannel.channelName
            tvChannelMemberCount.text = tpChannel.memberCount.toString()

            if (tpChannel.lastMessage != null) {
                tvLastMessage.text = tpChannel.lastMessage.text
                tvLastDate.text = lastFormattedTime(tpChannel.lastMessage.createdAt)
            }

            when (tpChannel.isPushNotificationDisabled) {
                true -> ivNotificationBell.setImageResource(R.drawable.ic_16_bell_off)
                false -> ivNotificationBell.setImageResource(R.drawable.ic_16_bell_on)
            }

            when (tpChannel.unreadCount == 0) {
                true -> tvChannelUnReadCount.visibility = View.GONE
                false -> tvChannelUnReadCount.text = if (tpChannel.unreadCount >= 999) "999+" else tpChannel.unreadCount.toString()
            }

            when (tpChannel.imageUrl.isNullOrEmpty()) {
                true -> ivChannelImage.loadThumbnail(R.drawable.ic_48_default_logo)
                false -> ivChannelImage.loadThumbnail(tpChannel.imageUrl)
            }

            itemView.setOnClickListener {
                onClick.invoke(tpChannel)
            }
        }

        private fun lastFormattedTime(timestamp: Long): String {
            val currentTime = Calendar.getInstance()
            val messageTime = Calendar.getInstance().apply { timeInMillis = timestamp }

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val dateFormat = SimpleDateFormat("MM월 d일", Locale.getDefault())

            val isToday = currentTime.get(Calendar.YEAR) == messageTime.get(Calendar.YEAR) &&
                currentTime.get(Calendar.DAY_OF_YEAR) == messageTime.get(Calendar.DAY_OF_YEAR)

            return if (isToday) timeFormat.format(messageTime.time) else dateFormat.format(messageTime.time)
        }
    }
}