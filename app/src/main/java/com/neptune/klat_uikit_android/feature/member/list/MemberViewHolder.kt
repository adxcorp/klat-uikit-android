package com.neptune.klat_uikit_android.feature.member.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ItemMembersBinding
import io.talkplus.entity.user.TPUser

class MemberViewHolder(
    private val binding: ItemMembersBinding,
    private val memberType: MemberType
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(tpMember: TPUser) = with(binding) {
        tvMemberNickname.text = tpMember.username
        ivMemberThumbnail.loadThumbnail(tpMember.profileImageUrl)

        when (memberType) {
            MemberType.MEMBER -> setMemberView(tpMember)
            MemberType.MUTED -> Unit
        }
    }

    private fun setMutedView() = with(binding) {
        tvBadgeMe.visibility = View.GONE
        tvBadgeOwner.visibility = View.GONE
    }

    private fun setMemberView(tpMember: TPUser) = with(binding) {
        when {
            tpMember.userId == ChannelObject.userId && tpMember.userId == ChannelObject.ownerId -> {
                tvBadgeMe.visibility = View.VISIBLE
                tvBadgeOwner.visibility = View.VISIBLE
            }
            tpMember.userId == ChannelObject.userId -> tvBadgeMe.visibility = View.VISIBLE
            tpMember.userId == ChannelObject.ownerId -> tvBadgeOwner.visibility = View.VISIBLE
        }
    }
}