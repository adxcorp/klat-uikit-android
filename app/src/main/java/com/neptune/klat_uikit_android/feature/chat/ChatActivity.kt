package com.neptune.klat_uikit_android.feature.chat

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.extension.showToast
import com.neptune.klat_uikit_android.core.ui.components.alert.AlertDialog
import com.neptune.klat_uikit_android.core.ui.components.alert.interfaces.MessageActions
import com.neptune.klat_uikit_android.core.ui.components.enums.StateType
import com.neptune.klat_uikit_android.core.ui.components.profile.ProfileDialog
import com.neptune.klat_uikit_android.core.util.CameraUtils
import com.neptune.klat_uikit_android.core.util.FileUtils
import com.neptune.klat_uikit_android.core.util.PermissionUtils
import com.neptune.klat_uikit_android.databinding.ActivityChatBinding
import com.neptune.klat_uikit_android.feature.channel.info.ChannelInfoActivity
import com.neptune.klat_uikit_android.feature.channel.main.ChannelActivity
import com.neptune.klat_uikit_android.feature.chat.emoji.EmojiBottomSheet
import com.neptune.klat_uikit_android.feature.chat.emoji.EmojiBottomSheet.MessageType
import com.neptune.klat_uikit_android.feature.chat.emoji.OnEmojiBottomSheetListener
import com.neptune.klat_uikit_android.feature.chat.photo.PhotoDetailActivity
import com.neptune.klat_uikit_android.feature.member.list.MemberInterface
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPMessage
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity(), MemberInterface, MessageActions, OnEmojiBottomSheetListener {
    companion object {
        private val BOTTOM_RANGE = 1..3
    }

    private lateinit var requestPermissionCameraLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionGalleryLauncher: ActivityResultLauncher<Array<String>>

    private val openCameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            ChannelObject.photoUri?.let { uri ->
                with(FileUtils) {
                    viewModel.sendFileMessage(resizeImage(getFileFromUri(this@ChatActivity, uri)))
                }
            }
        }
    }

    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { photoUri ->
        photoUri?.let { uri ->
            viewModel.sendFileMessage(FileUtils.resizeImage(FileUtils.getFileFromUri(this@ChatActivity, uri)))
        }
    }

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private val viewModel: ChatViewModel by viewModels()
    private val adapter: ChatAdapter by lazy { setAdapter() }

    private val onLayoutChangeListener = View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
        if (bottom < oldBottom) {
            binding.rvChat.scrollBy(0, oldBottom - bottom)
        }
    }

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            goBack()
            this.isEnabled = false
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!viewModel.isFirstLoad) {
                loadNextMessages(recyclerView)
            }
        }
    }

    private fun loadNextMessages(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (firstVisibleItemPosition == 3 && viewModel.currentItemCount != totalItemCount) {
            viewModel.currentItemCount = totalItemCount
            viewModel.getMessageList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        init()
    }

    private fun init() {
        binding.layoutChatEmpty.tvEmptyMessage.text = "아직 채널에 메시지가 없어요.\n제일 먼저 메시지를 남겨보세요."
        setRecyclerViewListener()
        viewModel.observeEvent()
        viewModel.getMessageList()
        setRequestLauncher()
        setHeaderUI()
        observeChatUiState()
        if (ChannelObject.tpChannel.isFrozen) setFrozenUI(true) else setMessageBarUI()
        if (ChannelObject.tpChannel.channelOwnerId == ChannelObject.userId) setMessageBarUI()
    }

    private fun observeChatUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.channelUiState.collect { chatUiState ->
                    when (chatUiState) {
                        is ChatUiState.BaseState -> {
                            when (chatUiState.baseState) {
                                is BaseUiState.Error -> showToast("${chatUiState.baseState.failedResult.errorCode}")
                                is BaseUiState.Loading -> { }
                                is BaseUiState.LoadingFinish -> { }
                            }
                        }
                        is ChatUiState.MarkAsRead -> adapter.updateUnreadCount()
                        is ChatUiState.SendMessage -> sendMessage(chatUiState.tpMessage)
                        is ChatUiState.DeleteMessage -> deletedMessage(chatUiState.tpMessage)
                        is ChatUiState.GetMessages -> loadMessages(chatUiState.tpMessages)
                        is ChatUiState.ReceiveMessage -> receiveMessage(chatUiState.tpMessage)
                        is ChatUiState.UpdatedReactionMessage -> updateReaction(chatUiState.tpMessage)
                        is ChatUiState.LeaveChannel -> finish()
                        is ChatUiState.RemoveChannel -> finish()
                        is ChatUiState.EmptyChat -> binding.layoutChatEmpty.root.visibility = View.VISIBLE
                        is ChatUiState.ChannelChanged -> channelChanged()
                    }
                }
            }
        }
    }

    private fun setHeaderUI() = with(binding) {
        layoutChatHeader.apply {
            ivLeftBtn.visibility = View.VISIBLE
            ivLeftBtn.setOnClickListener { goBack() }

            ivSecondRightBtn.visibility = View.VISIBLE
            ivSecondRightBtn.setImageResource(R.drawable.ic_24_info)
            ivSecondRightBtn.setOnClickListener {
                val intent = Intent(this@ChatActivity, ChannelInfoActivity::class.java)
                startActivity(intent)
            }

            tvMidText.visibility = View.VISIBLE
            tvMidText.text = ChannelObject.tpChannel.channelName
            tvMidText.setTextColor(Color.BLACK)
            tvMidText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
        }
    }

    private fun setFrozenUI(isFrozen: Boolean) = with(binding.layoutChatMessageBar) {
        binding.clChatFrozen.visibility = if (isFrozen) View.VISIBLE else View.GONE
        etInputMessage.isEnabled = !isFrozen
        etInputMessage.hint = if (isFrozen) "메시지 입력 불가" else "메시지 입력"
        etInputMessage.setHintTextColor(Color.parseColor(if (isFrozen) "#C1C1C1" else "#4D4D4D"))
        ivChatAttach.setColorFilter(Color.parseColor(if (isFrozen) "#C1C1C1" else "#000000"))
    }

    private fun setMessageBarUI() = with(binding.layoutChatMessageBar) {
        setAttackBlockUI()

        etInputMessage.addTextChangedListener { input ->
            ivChatSend.isVisible = !input.isNullOrEmpty()
        }

        ivChatSend.setOnClickListener {
            viewModel.setMyLastMessage(true)
            viewModel.sendMessage(etInputMessage.text.toString())
            etInputMessage.setText("")
            binding.rvChat.scrollToPosition(adapter.itemCount-1)
        }

        ivChatAttach.setOnClickListener {
            viewModel.setAttachMode(!viewModel.isAttachMode)

            ivChatAttach.loadThumbnail(if(viewModel.isAttachMode) R.drawable.ic_24_attach__close else R.drawable.ic_24_attach)

            when (viewModel.isAttachMode) {
                true -> binding.layoutChatAttach.clChatAttachBloack.visibility = View.VISIBLE
                false -> binding.layoutChatAttach.clChatAttachBloack.visibility = View.GONE
            }
        }
    }

    private fun setAttackBlockUI() = with(binding.layoutChatAttach) {
        clChatAlbum.setOnClickListener {
            PermissionUtils.checkGalleryPermission(requestPermissionGalleryLauncher)
        }

        clChatCamera.setOnClickListener {
            PermissionUtils.checkCameraPermission(
                context = this@ChatActivity,
                requestPermissionCameraLauncher = requestPermissionCameraLauncher
            )
        }
    }

    private fun setRecyclerViewListener() = with(binding) {
        rvChat.apply {
            adapter = this@ChatActivity.adapter
            addOnScrollListener(onScrollListener)
            addOnLayoutChangeListener(onLayoutChangeListener)
            itemAnimator = null
        }
    }

    private fun setAdapter(): ChatAdapter {
        return ChatAdapter(
            onLongClickMessage = { tpMessage, position ->
                val isMe = ChannelObject.userId == tpMessage.userId

                val messageType: MessageType = when {
                    tpMessage.fileUrl.isEmpty() -> if (isMe) MessageType.COPY_AND_DELETE else MessageType.COPY
                    isMe -> MessageType.DELETE
                    else -> MessageType.NONE
                }

                viewModel.setLongClickPosition(position)
                viewModel.setClickedTPMessage(tpMessage)

                EmojiBottomSheet(
                    messageType = messageType,
                    emojiSelectedListener = this
                ).show(supportFragmentManager, null)
            },

            onClickProfile = { tpMessage ->
                ProfileDialog(
                    profileImage =  tpMessage.userProfileImage,
                    userId = tpMessage.userId,
                    userNickname = tpMessage.username,
                    memberInterface = this
                ).show(supportFragmentManager, null)
            },

            onImageClick = {
                startActivity((Intent(this, PhotoDetailActivity::class.java)))
            }
        )
    }

    private fun loadMessages(newTPMessages: List<TPMessage>) {
        adapter.addMessages(newTPMessages)
        if (viewModel.isFirstLoad) {
            binding.rvChat.post { viewModel.setFirstLoad(false) }
            binding.rvChat.scrollToPosition(adapter.itemCount-1)
        }
    }

    private fun sendMessage(tpMessage: TPMessage) {
        binding.layoutChatEmpty.root.visibility = View.GONE
        adapter.addMessage(tpMessage)
        binding.rvChat.scrollToPosition(adapter.itemCount-1)
    }

    private fun receiveMessage(tpMessage: TPMessage) {
        binding.layoutChatEmpty.root.visibility = View.GONE
        adapter.addMessage(tpMessage)
        viewModel.setMyLastMessage(false)
        if (ChannelObject.tpChannel.unreadCount != 0 && !viewModel.isOnStop) viewModel.markAsRead()
        val layoutManager = binding.rvChat.layoutManager as LinearLayoutManager
        if (binding.rvChat.adapter?.itemCount?.minus(layoutManager.findLastVisibleItemPosition()) in BOTTOM_RANGE) {
            binding.rvChat.scrollToPosition(adapter.itemCount-1)
        }
    }

    private fun updateReaction(tpMessage: TPMessage) {
        adapter.updateReaction(tpMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        TalkPlus.removeChannelListener(viewModel.tag)
    }

    private fun goBack() {
        val intent = Intent(this, ChannelActivity::class.java)
        setResult(if (viewModel.isMyLastMessage) Activity.RESULT_OK else Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun setRequestLauncher() {
        requestPermissionGalleryLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when (PermissionUtils.checkMediaPermissions(permissions)) {
                true -> openGalleryLauncher.launch("image/*")
                false -> PermissionUtils.showPermissionRationale(
                    context = this,
                    title = "갤러리 권한 요청",
                    message = "권한을 허용해야 갤러리에 접근이 가능합니다."
                )
            }
        }

        requestPermissionCameraLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            when (isGranted) {
                true -> CameraUtils.openCamera(this, openCameraLauncher)
                false -> PermissionUtils.showPermissionRationale(
                    context = this,
                    title = "카메라 권한 요청",
                    message = "카메라 권한을 허용해야 사진 촬영이 가능합니다."
                )
            }
        }
    }

    private fun deletedMessage(deleteTPMessage: TPMessage) {
        adapter.deleteMessage(
            tpMessage = deleteTPMessage,
            deletePosition = viewModel.longClickPosition
        )
    }

    private fun channelChanged() {
        adapter.updateUnreadCount()
        binding.layoutChatHeader.tvMidText.text = ChannelObject.tpChannel.channelName
        if (ChannelObject.tpChannel.isFrozen) setFrozenUI(true) else {
            setFrozenUI(false)
            setMessageBarUI()
        }
    }

    override fun selectedEmoji(emoji: String) {
        viewModel.updateReaction(emoji)
    }

    override fun selectedCopyText() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        viewModel.copyMessage(clipboard)
    }

    override fun selectedDeleteText() {
        AlertDialog(
            stateType = StateType.DELETE_MESSAGE,
            messageActions = this
        ).show(supportFragmentManager, null)
    }

    override fun deleteMessage() {
        viewModel.deleteMessage()
    }

    override fun onResume() {
        super.onResume()
        viewModel.isOnStop = false
        if (ChannelObject.tpChannel.unreadCount != 0) {
            viewModel.markAsRead()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.isOnStop = true
    }
}