package com.neptune.klat_uikit_android.feature.channel.list

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ItemChannelBinding
import io.talkplus.entity.channel.TPChannel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChannelListAdapter(
    private val channelList: ArrayList<TPChannel>,
    private val searchKeyword: String = "",
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
            tvChannelMemberCount.text = tpChannel.memberCount.toString()

            if (searchKeyword.isNotEmpty()) {
                tvChannelName.text = highlightKeyword(
                    channelName = tvChannelName.text.toString(),
                    keyword = searchKeyword
                )
            } else {
                tvChannelName.text = tpChannel.channelName
            }

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
                false -> {
                    tvChannelUnReadCount.visibility = View.VISIBLE
                    tvChannelUnReadCount.text = if (tpChannel.unreadCount >= 999) "999+" else tpChannel.unreadCount.toString()
                }
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

        private fun highlightKeyword(
            channelName: String,
            keyword: String,
            color: Int = Color.parseColor("#00BFBF")
        ): SpannableString {
            val startIndex = channelName.indexOf(keyword, ignoreCase = true)
            if (startIndex == -1) {
                return SpannableString(channelName)
            }

            val spannableString = SpannableString(channelName)
            val endIndex = startIndex + keyword.length
            spannableString.setSpan(
                ForegroundColorSpan(color), // 강조할 색상
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannableString
        }
    }

    fun moveChannelItemToTop(tpChannel: TPChannel) {
        val oldChannelIndex: Int = channelList.indexOfFirst { it.channelId == tpChannel.channelId }
        if (oldChannelIndex == NO_INDEX) return
        channelList.removeAt(oldChannelIndex)
        channelList.add(FIRST, tpChannel)
        notifyItemMoved(oldChannelIndex, FIRST)
        notifyItemChanged(FIRST)
        ChannelObject.setTPChannels(channelList)
    }

    fun addChannelItemToTop(tpChannel: TPChannel) {
        channelList.add(FIRST, tpChannel)
        notifyItemInserted(FIRST)
        ChannelObject.setTPChannels(channelList)
    }

    fun updateChannelItem(tpChannel: TPChannel) {
        val updateTPChannelIndex: Int = channelList.indexOfFirst { it.channelId == tpChannel.channelId }
        if (updateTPChannelIndex == NO_INDEX) return
        channelList[updateTPChannelIndex] = tpChannel
        notifyItemChanged(updateTPChannelIndex)
        ChannelObject.setTPChannels(channelList)
    }

    fun removeChannelItem(tpChannel: TPChannel) {
        val removeChannelIndex: Int = channelList.indexOfFirst { it.channelId == tpChannel.channelId }
        if (removeChannelIndex == NO_INDEX) return
        channelList.removeAt(removeChannelIndex)
        notifyItemRemoved(removeChannelIndex)
        ChannelObject.setTPChannels(channelList)
    }

    companion object {
        private const val FIRST = 0
        private const val NO_INDEX = -1
    }
}