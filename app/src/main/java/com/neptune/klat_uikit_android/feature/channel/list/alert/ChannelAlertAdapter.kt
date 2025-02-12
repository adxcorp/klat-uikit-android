package com.neptune.klat_uikit_android.feature.channel.list.alert

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.databinding.ItemChannelOptionBinding

class ChannelAlertAdapter(
    private val options: List<String> = listOf("읽음", "나가기"),
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ChannelAlertAdapter.ChannelAlertViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelAlertViewHolder {
        return ChannelAlertViewHolder(ItemChannelOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: ChannelAlertViewHolder, position: Int) {
        holder.bind(options[position])
    }

    inner class ChannelAlertViewHolder(private val binding: ItemChannelOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) = with(binding) {
            tvOptionTitle.text = title
            itemView.setOnClickListener {
                onClick.invoke(adapterPosition)
            }
        }
    }

    companion object {
        const val MARK_AS_READ = 0
        const val LEAVE_CHANNEL = 1
    }
}