package com.neptune.klat_uikit_android.core.base

sealed class BaseUiState {
    object Init : BaseUiState()
    object Loading : BaseUiState()
    object Empty : BaseUiState()
    data class Error(val message: String) : BaseUiState()
}