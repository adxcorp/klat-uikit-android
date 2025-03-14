package com.neptune.klat_uikit_android.feature.member.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.databinding.ItemMembersBinding
import io.talkplus.entity.user.TPUser

class MemberAdapter(
    private val members: ArrayList<TPUser>,
    private val memberType: MemberType = MemberType.MEMBER,
    private val onClick: (TPUser) -> Unit
) : RecyclerView.Adapter<MemberViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMembersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(
            memberType = memberType,
            binding = binding,
            onClick = onClick
        )
    }

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    fun addMembers(newMembers: List<TPUser>) {
        val startPosition = members.size
        members.addAll(newMembers)
        notifyItemRangeInserted(startPosition, newMembers.size)
    }

    fun removeMember(banId: String) {
        val removeTPUser = members.find { it.userId == banId }
        val removePosition = members.indexOf(removeTPUser)
        members.removeAt(removePosition)
        notifyItemRemoved(removePosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMembers(newMembers: List<TPUser>) {
        members.apply {
            clear()
            addAll(newMembers)
        }
        notifyDataSetChanged()
    }
}

enum class MemberType {
    MUTED,
    MEMBER
}