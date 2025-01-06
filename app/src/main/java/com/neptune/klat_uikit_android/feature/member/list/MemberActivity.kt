package com.neptune.klat_uikit_android.feature.member.list

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.neptune.klat_uikit_android.core.base.BaseActivity
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.databinding.ActivityMemberBinding
import kotlinx.coroutines.launch

class MemberActivity : BaseActivity<ActivityMemberBinding>() {
    private val viewModel: MemberViewModel by viewModels()
    private val adapter: MutedAdapter by lazy { setAdapter() }

    override fun bindingFactory(): ActivityMemberBinding {
        return ActivityMemberBinding.inflate(layoutInflater)
    }

    override fun init() {
        observeUiState()
        setMemberViewType()
        setClickListener()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.memberUiState.collect { memberUiState ->
                    handleUiState(memberUiState)
                }
            }
        }
    }

    private fun handleUiState(memberUiState: MemberUiState) {
        when (memberUiState) {
            is MemberUiState.BaseState -> { }
            is MemberUiState.GetMutesMembers -> adapter.addMembers(memberUiState.tpMembers)
        }
    }

    private fun setMutedType() {
        binding.tvMemberTitle.text = "음소거 목록"
        when (intent.getBooleanExtra(EXTRA_IS_OWNER, false)) {
            true -> viewModel.getMutedUsers()
            false -> viewModel.getPeerMutedUsers()
        }
    }

    private fun setAdapter(): MutedAdapter {
        return MutedAdapter(
            memberType = if(intent.getBooleanExtra(EXTRA_MEMBER, false)) MemberType.MEMBER else MemberType.MUTED,
            members = if (intent.getBooleanExtra(EXTRA_MEMBER, false)) {
                 ArrayList(ChannelObject.tpChannel.members.sortedBy { tpMember ->
                     when {
                         tpMember.userId == ChannelObject.userId -> FIRST_INDEX
                         ChannelObject.tpChannel.channelOwnerId == tpMember.userId -> SECOND_INDEX
                         else -> OTHERS
                     }
                 })
            } else arrayListOf()
        )
    }

    private fun setClickListener() = with(binding) {
        ivMemberClose.setOnClickListener { finish() }
    }

    private fun setMemberViewType() {
        when (intent.getBooleanExtra(EXTRA_MEMBER, false)) {
            true -> Unit
            false -> setMutedType()
        }
        binding.rvMembers.adapter = adapter
    }

    companion object {
        const val EXTRA_MEMBER = "extra_member"
        const val EXTRA_IS_OWNER = "extra_is_owner"

        private const val FIRST_INDEX = 0
        private const val SECOND_INDEX = 1
        private const val OTHERS = 2
    }
}