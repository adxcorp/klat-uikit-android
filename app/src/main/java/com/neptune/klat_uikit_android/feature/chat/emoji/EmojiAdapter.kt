package com.neptune.klat_uikit_android.feature.chat.emoji

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.databinding.ItemEmojiBinding

class EmojiAdapter(
    private val emojis: List<String> = listOf("😀", "😎", "👍", "😍", "✅", "👎", "😂", "👏", "♥️", "👀"),
    private val onClick: (String) -> Unit,
) : RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val binding = ItemEmojiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmojiViewHolder(binding)
    }

    override fun getItemCount(): Int = emojis.size

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojis[position])
    }

    inner class EmojiViewHolder(private val binding: ItemEmojiBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind(emoji: String) = with(binding) {
            tvEmoji.text = emoji
            itemView.setOnClickListener { onClick.invoke(emoji) }
        }
    }
}
