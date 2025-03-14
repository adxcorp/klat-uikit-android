package com.neptune.klat_uikit_android.feature.channel.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.ui.components.alert.AlertDialog
import com.neptune.klat_uikit_android.core.ui.components.alert.interfaces.ChannelActions
import com.neptune.klat_uikit_android.core.ui.components.enums.StateType
import com.neptune.klat_uikit_android.databinding.FragmentChannelListBinding
import com.neptune.klat_uikit_android.feature.channel.create.ChannelCreateActivity
import com.neptune.klat_uikit_android.feature.channel.list.alert.ChannelLongClickAlert
import com.neptune.klat_uikit_android.feature.channel.list.alert.ChannelLongClickListener
import com.neptune.klat_uikit_android.feature.channel.search.ChannelSearchActivity
import com.neptune.klat_uikit_android.feature.chat.ChatActivity
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import kotlinx.coroutines.launch

class ChannelListFragment : Fragment(), ChannelLongClickListener, ChannelActions {
    private var _binding: FragmentChannelListBinding? = null
    private val binding get() = _binding ?: error("FragmentChannelListBinding 초기화 에러")

    private val parentActivity: FragmentActivity by lazy { requireActivity() }

    private val viewModel: ChannelListViewModel by viewModels()

    private val adapter: ChannelListAdapter by lazy { setChannelListAdapter() }

    // 내가 보낸 메시지 갱신시키기 위함
    private val channelUpdateLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    viewModel.getChannel()
                }
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChannelListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setHeaderUI()
        observeChannelListUiState()
    }

    override fun onResume() {
        super.onResume()
        if (!ChannelObject.tpChannel.channelId.isNullOrEmpty()) {
            adapter.updateChannelItem(ChannelObject.tpChannel)
        }
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
            is ChannelUiState.AddMember -> addMember(channelUiState.tpChannel)
            is ChannelUiState.MarkAsRead -> {
                adapter.updateChannelItem(ChannelObject.tpChannel)
                if (!viewModel.isLongClickMarkAsRead) {
                    moveChatScreen()
                }
                viewModel.isLongClickMarkAsRead = false
            }
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
                val intent = Intent(parentActivity, ChannelCreateActivity::class.java).apply {
                    putExtra(ChannelCreateActivity.EXTRA_TYPE, ChannelCreateActivity.CREATE)
                }
                startActivity(intent)
            }
        }
    }

    private fun showEmptyChannelUI() = with(binding) {
        layoutEmpty.root.visibility = View.VISIBLE
        layoutEmpty.tvEmptyMessage.text = getString(R.string.empty_channel)
        layoutHeader.ivFirstRightBtn.isEnabled = false
    }

    private fun setChannelListAdapter(): ChannelListAdapter {
        return ChannelListAdapter(
            viewModel.currentChannelList,
            onClick = { tpChannel ->
                ChannelObject.setTPChannel(tpChannel)
                if (tpChannel.unreadCount != 0) viewModel.markAsRead() else moveChatScreen()
            },
            onLongClick = { tpChannel ->
                ChannelObject.setTPChannel(tpChannel)
                ChannelLongClickAlert(
                    channelName = tpChannel.channelName,
                    channelLongClickListener = this
                ).show(parentFragmentManager, null)
            },
        )
    }

    private fun moveChatScreen() {
        val intent = Intent(parentActivity, ChatActivity::class.java)
        channelUpdateLauncher.launch(intent)
    }

    private fun addMember(tpChannel: TPChannel) {
        if (ChannelObject.tpChannel.channelOwnerId == ChannelObject.userId) {
            adapter.updateChannelItem(tpChannel)
        } else {
            adapter.addChannelItemToTop(tpChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TalkPlus.removeChannelListener(ChannelObject.tag)
    }

    override fun markAsRead() {
        viewModel.isLongClickMarkAsRead = true
        if (ChannelObject.tpChannel.unreadCount != 0) viewModel.markAsRead()
    }

    override fun leaveChannel() {
        AlertDialog(
            stateType = StateType.CHANNEL_LEAVE,
            title = ChannelObject.tpChannel.channelName,
            channelActions = this
        ).show(parentFragmentManager, null)
    }

    override fun leaveChannelToList() {
        viewModel.deleteChannel()
    }
}