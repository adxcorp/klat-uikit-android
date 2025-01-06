package com.neptune.klat_uikit_android.feature.member.list

import com.neptune.klat_uikit_android.core.base.BaseUiState
import io.talkplus.entity.user.TPUser

sealed class MemberUiState {
    data class BaseState(val baseState: BaseUiState) : MemberUiState()
    data class GetMutesMembers(val tpMembers: List<TPUser>) : MemberUiState()
}