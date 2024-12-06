package com.neptune.klat_uikit_android.feature.channel.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChannelSearchViewModel : ViewModel() {
    val searchChannelList: ArrayList<TPChannel> = arrayListOf()

    private var _searchUiState = MutableSharedFlow<SearchUiState>()
    val searchUiState: SharedFlow<SearchUiState>
        get() = _searchUiState.asSharedFlow()

    fun searchChannelByName(channelName: String) {
        if (channelName != "") {
            viewModelScope.launch {
                val searchResult: List<TPChannel> = searchChannelList.filter { it.channelName.contains(channelName) }
                when (searchResult.isEmpty()) {
                    true -> _searchUiState.emit(SearchUiState.SearchResultEmpty)
                    false -> _searchUiState.emit(SearchUiState.SearchResult(searchChannelList.filter { it.channelName.contains(channelName) }))
                }
            }
        }
    }
}