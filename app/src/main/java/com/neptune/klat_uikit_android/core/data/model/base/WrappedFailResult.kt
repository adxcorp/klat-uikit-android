package com.neptune.klat_uikit_android.core.data.model.base

data class WrappedFailResult (
    val stateName: String = "",
    val errorCode: Int,
    val exception: Exception
)