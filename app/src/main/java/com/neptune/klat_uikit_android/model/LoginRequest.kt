package com.neptune.klat_uikit_android.model

data class LoginRequest(
    val userToken: String? = null,
    val userId: String,
    val userNickname: String,
    val profileImage: String = ""
)
