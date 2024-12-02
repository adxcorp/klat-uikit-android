package com.neptune.klat_uikit_android.core.base

import com.neptune.klat_uikit_android.core.data.model.base.WrappedFailResult

sealed class BaseUiState {
    object Loading : BaseUiState()
    object LoadingFinish : BaseUiState()
    data class Error(val failedResult: WrappedFailResult) : BaseUiState()
}