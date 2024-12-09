package com.neptune.klat_uikit_android.feature.channel.list

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.databinding.FragmentChannelListBinding
import com.neptune.klat_uikit_android.feature.channel.create.ChannelCreateActivity
import com.neptune.klat_uikit_android.feature.channel.search.ChannelSearchActivity
import com.neptune.klat_uikit_android.feature.chat.ChatActivity
import kotlinx.coroutines.launch

class ChannelListFragment : Fragment(), SwipeCallbackListener {
    private var _binding: FragmentChannelListBinding? = null
    private val binding get() = _binding ?: error("FragmentChannelListBinding 초기화 에러")
    private val parentActivity: FragmentActivity by lazy { requireActivity() }
    private val viewModel: ChannelListViewModel by viewModels()
    private val adapter: ChannelListAdapter by lazy { setChannelListAdapter() }

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
        setSwipeListener()
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
                val intent: Intent = Intent(parentActivity, ChannelSearchActivity::class.java).apply {
                    putExtra(ChannelSearchActivity.EXTRA_CHANNEL_LIST, viewModel.currentChannelList)
                }
                startActivity(intent)
            }
        }

        layoutHeader.ivSecondRightBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_add_channel)
            setOnClickListener {
                val intent: Intent = Intent(parentActivity, ChannelCreateActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setChannelList() {

    }

    private fun showEmptyChannelUI() = with(binding) {
        layoutEmpty.root.visibility = View.VISIBLE
        layoutEmpty.tvEmptyMessage.text = getString(R.string.empty_channel)
        layoutHeader.ivFirstRightBtn.isEnabled = false
    }

    private fun setChannelListAdapter(): ChannelListAdapter {
        return ChannelListAdapter(viewModel.currentChannelList) { tpChannel ->
            val intent = Intent(parentActivity, ChatActivity::class.java).apply {
                putExtra(ChatActivity.EXTRA_CHANNEL_ID, tpChannel.channelId)
            }
            startActivity(intent)
        }
    }

    private fun setSwipeListener() {
        ItemTouchHelper(ItemSwipeCallback(
            swipeCallbackListener = this,
            context = parentActivity
        )).attachToRecyclerView(binding.rvChannels)
    }

    override fun onSwipe(position: Int) {

    }
}