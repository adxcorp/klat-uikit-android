package com.neptune.klat_uikit_android.core.data.model.base

sealed class Result<out T : Any, out U : Any> {
    data class Success<T : Any>(val successData: T) : Result<T, Nothing>()
    data class Failure<U : Any>(val failResult: U) : Result<Nothing, U>()
}

