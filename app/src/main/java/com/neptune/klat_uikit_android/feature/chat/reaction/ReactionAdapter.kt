package com.neptune.klat_uikit_android.feature.chat.reaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.databinding.ItemReactionBinding
import io.talkplus.entity.channel.TPMessage
import kotlin.collections.ArrayList

class ReactionAdapter(
    private val tpMessage: TPMessage
) : RecyclerView.Adapter<ReactionAdapter.ReactionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReactionViewHolder {
        return ReactionViewHolder(ItemReactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return tpMessage.reactions.size
    }

    override fun onBindViewHolder(holder: ReactionViewHolder, position: Int) {
        val keys = tpMessage.reactions.keys.toMutableList()
        holder.bind(emoji = keys[position])
    }

    inner class ReactionViewHolder(private val binding: ItemReactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(emoji: String) = with(binding) {
            val reactionUsers: ArrayList<String> = tpMessage.reactions?.get(emoji) ?: arrayListOf()

            if (reactionUsers.contains(ChannelObject.userId)) {
                root.setBackgroundResource(R.drawable.bg_reaction_selected)
                tvReactionCount.setTextColor(binding.root.context.resources.getColor(R.color.klat_brand_color, null))
            } else {
                root.setBackgroundResource(R.drawable.bg_reaction_un_selected)
                tvReactionCount.setTextColor(Color.BLACK)
            }
            tvReactionEmoji.text = emoji
            tvReactionCount.text = reactionUsers.size.toString()
        }
    }
}