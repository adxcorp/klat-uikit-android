package com.neptune.klat_uikit_android.feature.chat.photo

sealed class PhotoDetailUiState {
    object None : PhotoDetailUiState()
    object Exception : PhotoDetailUiState()
    object DownloadSuccess : PhotoDetailUiState()
    object Downloading : PhotoDetailUiState()
    object DownloadFailed : PhotoDetailUiState()
}