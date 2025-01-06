package com.neptune.klat_uikit_android.feature.member.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ItemMembersBinding
import io.talkplus.entity.user.TPUser

class MemberViewHolder(
    private val binding: ItemMembersBinding,
    private val memberType: MemberType,
    private val onClick: (TPUser) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(tpMember: TPUser) = with(binding) {
        tvMemberNickname.text = tpMember.username
        ivMemberThumbnail.loadThumbnail(tpMember.profileImageUrl)

        itemView.setOnClickListener {
            onClick.invoke(tpMember)
        }

        if (memberType == MemberType.MEMBER) {
            setMemberView(tpMember)
        }
    }

    private fun setMemberView(tpMember: TPUser) = with(binding) {
        when {
            tpMember.userId == ChannelObject.userId && tpMember.userId == ChannelObject.tpChannel.channelOwnerId -> {
                tvBadgeMe.visibility = View.VISIBLE
                tvBadgeOwner.visibility = View.VISIBLE
            }
            tpMember.userId == ChannelObject.userId -> tvBadgeMe.visibility = View.VISIBLE
            tpMember.userId == ChannelObject.tpChannel.channelOwnerId -> tvBadgeOwner.visibility = View.VISIBLE
        }
    }
}