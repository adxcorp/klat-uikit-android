package com.neptune.klat_uikit_android.feature.channel.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.neptune.klat_uikit_android.core.util.hideKeyboard
import com.neptune.klat_uikit_android.core.util.showKeyboard
import com.neptune.klat_uikit_android.databinding.ActivityChannelSearchBinding
import com.neptune.klat_uikit_android.feature.channel.list.ChannelListViewModel

class ChannelSearchActivity : AppCompatActivity() {
    private val viewModel: ChannelListViewModel by viewModels()
    private val binding: ActivityChannelSearchBinding by lazy { ActivityChannelSearchBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSearchUI()
        setKeyBoardEvent()
    }

    private fun setSearchUI() = with(binding) {
        binding.ivSearchClose.setOnClickListener { finish() }
        binding.layoutChannelSearch.etSearch.requestFocus()

        showKeyboard(binding.layoutChannelSearch.etSearch)

        layoutChannelSearch.tvSearchCancel.text = "검색"

        layoutChannelSearch.etSearch.addTextChangedListener { input ->
            layoutChannelSearch.tvSearchCancel.isVisible = !input.isNullOrEmpty()
        }

        layoutChannelSearch.tvSearchCancel.setOnClickListener {
            hideKeyboard(layoutChannelSearch.etSearch)
        }
    }

    private fun setKeyBoardEvent() {
        binding.layoutChannelSearch.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(binding.layoutChannelSearch.etSearch)
                true
            } else {
                false
            }
        }
    }
}