package com.neptune.klat_uikit_android.core.data.model.member

import io.talkplus.entity.user.TPUser

data class MemberResponse(
    val tpMembers: List<TPUser>,
    val hasNext: Boolean
)