package com.neptune.klat_uikit_android.feature.channel.search

import io.talkplus.entity.channel.TPChannel

sealed class SearchUiState {
    data class SearchResult(val result: List<TPChannel>) : SearchUiState()
    object SearchResultEmpty : SearchUiState()
}