package com.neptune.klat_uikit_android.feature.channel.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.util.hideKeyboard
import com.neptune.klat_uikit_android.core.util.showKeyboard
import com.neptune.klat_uikit_android.databinding.FragmentChannelListBinding
import kotlinx.coroutines.launch

class ChannelListFragment : Fragment() {
    private var _binding: FragmentChannelListBinding? = null
    private val binding get() = _binding ?: error("FragmentChannelListBinding 초기화 에러")
    private val parentActivity: FragmentActivity by lazy { requireActivity() }
    private val viewModel: ChannelListViewModel by viewModels()
    private val adapter: ChannelListAdapter by lazy { ChannelListAdapter(viewModel.currentChannelList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChannelListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getChannelList()
        setHeaderUI()
        observeChannelList()
    }

    private fun observeChannelList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.channelUiState.collect { channelUiState ->
                    handleChannelUiState(channelUiState)
                }
            }
        }
    }

    private fun handleChannelUiState(uiState: ChannelUiState) {
        when (uiState) {
            is ChannelUiState.BaseState -> {
                when (uiState.baseState) {
                    is BaseUiState.Loading -> {}
                    is BaseUiState.LoadingFinish -> {}
                    is BaseUiState.Error -> {}
                }
            }

            is ChannelUiState.ChannelListEmpty -> showEmptyChannelUI()
            is ChannelUiState.GetChannelList -> binding.rvChannels.adapter = this.adapter
        }
    }

    private fun setHeaderUI() = with(binding) {
        layoutHeader.ivLeftBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_back)
            setOnClickListener { parentActivity.finish() }
        }

        layoutHeader.tvLeftText.apply {
            visibility = View.VISIBLE
            text = "채널"
        }

        layoutHeader.ivFirstRightBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_search)
            setOnClickListener {
                setSearchUI()
            }
        }

        layoutHeader.ivSecondRightBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_add_channel)
            setOnClickListener {
                // TODO 채널 추가
            }
        }
    }

    private fun setSearchUI() = with(binding) {
        layoutSearch.root.visibility = View.VISIBLE
        layoutSearch.etSearch.requestFocus()

        parentActivity.showKeyboard(layoutSearch.etSearch)

        layoutSearch.etSearch.addTextChangedListener { input ->
            layoutSearch.tvSearchCancel.isVisible = !input.isNullOrEmpty()
        }

        layoutSearch.tvSearchCancel.setOnClickListener {
            layoutSearch.etSearch.setText("")
            layoutSearch.root.visibility = View.GONE

            parentActivity.hideKeyboard(layoutSearch.etSearch)
        }
    }

    private fun setChannelList() {

    }

    private fun showEmptyChannelUI() {
        binding.layoutEmpty.root.visibility = View.VISIBLE
        binding.layoutEmpty.tvEmptyMessage.text = getString(R.string.empty_channel)
    }
}