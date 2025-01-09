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
        tvBadgeMe.visibility = View.GONE
        tvBadgeOwner.visibility = View.GONE

        val currentUserId = tpMember.userId
        val ownerId = ChannelObject.tpChannel.channelOwnerId
        val myId = ChannelObject.userId

        tvBadgeMe.visibility = if (currentUserId == myId) View.VISIBLE else View.GONE
        tvBadgeOwner.visibility = if (currentUserId == ownerId) View.VISIBLE else View.GONE
    }
}