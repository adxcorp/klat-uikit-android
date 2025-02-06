package com.neptune.klat_uikit_android.feature.member.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.model.member.MemberResponse
import com.neptune.klat_uikit_android.core.data.repository.member.MemberRepository
import io.talkplus.entity.user.TPUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MemberViewModel(private val memberRepository: MemberRepository = MemberRepository()) : ViewModel() {
    companion object {
        private const val FIRST_INDEX = 0
        private const val SECOND_INDEX = 1
        private const val OTHERS = 2
    }

    private var lastUser: TPUser? = null
    private var hasNext: Boolean = true

    private var _memberUiState = MutableSharedFlow<MemberUiState>()
    val memberUiState: SharedFlow<MemberUiState>
        get() = _memberUiState.asSharedFlow()

    fun getPeerMutedUsers() {
        if (!hasNext) return

        viewModelScope.launch {
            memberRepository.getPeerMutedMembers(lastUser).collect { callbackResult ->
                when(callbackResult) {
                    is Result.Success -> emitMemberUiState(callbackResult.successData)
                    is Result.Failure -> _memberUiState.emit(MemberUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun getMutedUsers() {
        if (!hasNext) return

        viewModelScope.launch {
            memberRepository.getMutedMembers(
                lastUser = lastUser,
                tpChannel = ChannelObject.tpChannel
            ).collect { callbackResult ->
                when(callbackResult) {
                    is Result.Success -> {
                        Log.d("!! : ", callbackResult.successData.tpMembers.toString())
                        emitMemberUiState(callbackResult.successData)
                    }
                    is Result.Failure -> _memberUiState.emit(MemberUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    private suspend fun emitMemberUiState(memberResponse: MemberResponse) {
        hasNext = memberResponse.hasNext
        lastUser = memberResponse.tpMembers.lastOrNull()
        _memberUiState.emit(MemberUiState.GetMutesMembers(memberResponse.tpMembers))
    }

    fun sortOwnerAndMe(): ArrayList<TPUser> {
        return ArrayList(ChannelObject.tpChannel.members.sortedBy { tpMember ->
            when {
                tpMember.userId == ChannelObject.userId -> FIRST_INDEX
                ChannelObject.tpChannel.channelOwnerId == tpMember.userId -> SECOND_INDEX
                else -> OTHERS
            }
        })
    }
}