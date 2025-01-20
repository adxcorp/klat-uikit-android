package com.neptune.klat_uikit_android.feature.channel.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.FragmentChannelListBinding
import com.neptune.klat_uikit_android.feature.channel.create.ChannelCreateActivity
import com.neptune.klat_uikit_android.feature.channel.search.ChannelSearchActivity
import com.neptune.klat_uikit_android.feature.chat.ChatActivity
import io.talkplus.TalkPlus
import io.talkplus.TalkPlus.CallbackListener
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage
import kotlinx.coroutines.launch


class ChannelListFragment : Fragment(), SwipeCallbackListener {
    private var _binding: FragmentChannelListBinding? = null
    private val binding get() = _binding ?: error("FragmentChannelListBinding 초기화 에러")

    private val parentActivity: FragmentActivity by lazy { requireActivity() }

    private val viewModel: ChannelListViewModel by viewModels()

    private val adapter: ChannelListAdapter by lazy { setChannelListAdapter() }

    private val channelUpdateLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    viewModel.getChannel()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChannelListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setHeaderUI()
        setSwipeListener()
        observeChannelListUiState()
    }

    private fun init() = with(viewModel) {
        binding.rvChannels.itemAnimator = null
        observeChannelList()
        getChannels()
    }

    private fun observeChannelListUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.channelUiState.collect { channelUiState ->
                    handleChannelUiState(channelUiState)
                }
            }
        }
    }

    private fun handleChannelUiState(channelUiState: ChannelUiState) {
        when (channelUiState) {
            is ChannelUiState.BaseState -> {
                when (channelUiState.baseState) {
                    is BaseUiState.Loading -> { }
                    is BaseUiState.LoadingFinish -> { }
                    is BaseUiState.Error ->  { }
                }
            }
            is ChannelUiState.ChannelListEmpty -> showEmptyChannelUI()
            is ChannelUiState.GetChannelList -> binding.rvChannels.adapter = this.adapter
            is ChannelUiState.ReceivedMessage -> adapter.moveChannelItemToTop(channelUiState.tpChannel)
            is ChannelUiState.AddedChannel -> adapter.addChannelItemToTop(channelUiState.tpChannel)
            is ChannelUiState.RemovedChannel -> adapter.removeChannelItem(channelUiState.tpChannel)
            is ChannelUiState.ChangedChannel -> adapter.updateChannelItem(channelUiState.tpChannel)
            is ChannelUiState.BanUser -> adapter.updateChannelItem(channelUiState.tpChannel)
            is ChannelUiState.LeaveChannel -> adapter.removeChannelItem(channelUiState.tpChannel)
            is ChannelUiState.GetChannel -> adapter.moveChannelItemToTop(channelUiState.tpChannel)
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
            loadThumbnail(R.drawable.ic_24_add_channel)
            setOnClickListener {
                startActivity(Intent(parentActivity, ChannelCreateActivity::class.java))
            }
        }
    }

    private fun showEmptyChannelUI() = with(binding) {
        layoutEmpty.root.visibility = View.VISIBLE
        layoutEmpty.tvEmptyMessage.text = getString(R.string.empty_channel)
        layoutHeader.ivFirstRightBtn.isEnabled = false
    }

    private fun setChannelListAdapter(): ChannelListAdapter {
        return ChannelListAdapter(viewModel.currentChannelList) { tpChannel ->
            ChannelObject.setTPChannel(tpChannel)
            val intent = Intent(parentActivity, ChatActivity::class.java)
            channelUpdateLauncher.launch(intent)
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

    override fun onDestroy() {
        super.onDestroy()
        TalkPlus.removeChannelListener(ChannelObject.tag)
    }
}