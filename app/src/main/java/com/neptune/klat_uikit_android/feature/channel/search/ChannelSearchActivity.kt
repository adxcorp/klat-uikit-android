package com.neptune.klat_uikit_android.feature.channel.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.extension.getSerializableList
import com.neptune.klat_uikit_android.core.extension.hideKeyboard
import com.neptune.klat_uikit_android.core.extension.showKeyboard
import com.neptune.klat_uikit_android.databinding.ActivityChannelSearchBinding
import com.neptune.klat_uikit_android.feature.channel.list.ChannelListAdapter
import com.neptune.klat_uikit_android.feature.chat.ChatActivity
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.launch

class ChannelSearchActivity : AppCompatActivity() {
    private val viewModel: ChannelSearchViewModel by viewModels()
    private val binding: ActivityChannelSearchBinding by lazy { ActivityChannelSearchBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        setSearchUI()
        setKeyBoardEvent()
        observeSearchUiState()
    }

    private fun init() {
        binding.layoutSearchEmpty.apply {
            tvEmptyMessage.text = "검색 결과가 없습니다."
            ivEmptyLogo.setImageResource(R.drawable.ic_40_no_searh_result)
        }
        viewModel.searchChannelList.addAll(intent.getSerializableList(EXTRA_CHANNEL_LIST) ?: arrayListOf())
    }

    private fun observeSearchUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchUiState.collect { searchUiState ->
                    handleSearchUiState(searchUiState)
                }
            }
        }
    }

    private fun handleSearchUiState(searchUiState: SearchUiState) {
        when (searchUiState) {
            is SearchUiState.SearchResult -> {
                binding.rvSearchChannelList.adapter = setChannelListAdapter(searchUiState.result)
                binding.layoutSearchEmpty.root.visibility = View.GONE
            }
            is SearchUiState.SearchResultEmpty -> {
                binding.rvSearchChannelList.adapter = ChannelListAdapter(arrayListOf()) { }
                binding.layoutSearchEmpty.root.visibility = View.VISIBLE
            }
        }
    }

    private fun setSearchUI() = with(binding) {
        binding.ivSearchClose.setOnClickListener { finish() }
        binding.layoutChannelSearch.etSearch.requestFocus()

        showKeyboard(binding.layoutChannelSearch.etSearch)

        layoutChannelSearch.tvRight.text = "검색"

        layoutChannelSearch.etSearch.addTextChangedListener { input ->
            layoutChannelSearch.tvRight.isVisible = !input.isNullOrEmpty()
        }

        layoutChannelSearch.tvRight.setOnClickListener {
            hideKeyboard(layoutChannelSearch.etSearch)
            viewModel.searchChannelByName(binding.layoutChannelSearch.etSearch.text.toString())
        }
    }

    private fun setKeyBoardEvent() {
        binding.layoutChannelSearch.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard(binding.layoutChannelSearch.etSearch)
                viewModel.searchChannelByName(binding.layoutChannelSearch.etSearch.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun setChannelListAdapter(channelList: List<TPChannel>): ChannelListAdapter {
        return ChannelListAdapter(channelList as ArrayList<TPChannel>) { tpChannel ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHANNEL_ID, tpChannel.channelId)
            }
            startActivity(intent)
        }
    }

    companion object {
        const val EXTRA_CHANNEL_LIST = "extra_channel_list"
    }
}