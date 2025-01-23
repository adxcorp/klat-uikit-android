package com.neptune.klat_uikit_android.feature.chat

import android.app.Activity
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
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.ui.components.profile.ProfileDialog
import com.neptune.klat_uikit_android.core.util.CameraUtils
import com.neptune.klat_uikit_android.core.util.FileUtils
import com.neptune.klat_uikit_android.core.util.PermissionUtils
import com.neptune.klat_uikit_android.databinding.ActivityChatBinding
import com.neptune.klat_uikit_android.feature.channel.info.ChannelInfoActivity
import com.neptune.klat_uikit_android.feature.channel.main.ChannelActivity
import com.neptune.klat_uikit_android.feature.chat.emoji.EmojiBottomSheet
import com.neptune.klat_uikit_android.feature.chat.emoji.OnEmojiSelectedListener
import com.neptune.klat_uikit_android.feature.member.list.MemberInterface
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPMessage
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity(), MemberInterface, OnEmojiSelectedListener {
    companion object {
        private const val BOTTOM = 0
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
            loadNextMessages(recyclerView)
        }
    }

    private fun loadNextMessages(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        if (firstVisibleItemPosition + visibleItemCount >= totalItemCount - 10) {
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
        viewModel.getMessageList()
        viewModel.observeEvent()
        setRequestLauncher()
        bindView()
        setHeaderUI()
        setRecyclerViewListener()
        observeChatUiState()
        if (ChannelObject.tpChannel.isFrozen) setFrozenUI(true) else setMessageBarUI()
        if (ChannelObject.tpChannel.channelOwnerId == ChannelObject.userId) setMessageBarUI()
    }

    private fun bindView() = with(binding) {

    }

    private fun observeChatUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.channelUiState.collect { chatUiState ->
                    when (chatUiState) {
                        is ChatUiState.BaseState -> {

                        }

                        is ChatUiState.SendMessage -> sendMessage(chatUiState.tpMessage)
                        is ChatUiState.GetMessages -> loadMessages(chatUiState.tpMessages)
                        is ChatUiState.ReceiveMessage -> receiveMessage(chatUiState.tpMessage)
                        is ChatUiState.UpdatedReactionMessage -> updateReaction(chatUiState.tpMessage)
                        is ChatUiState.Frozen -> if (ChannelObject.tpChannel.isFrozen) setFrozenUI(true) else {
                            setFrozenUI(false)
                            setMessageBarUI()
                        }
                        is ChatUiState.LeaveChannel -> finish()
                        is ChatUiState.RemoveChannel -> finish()
                        is ChatUiState.EmptyChat -> binding.layoutChatEmpty.root.visibility = View.VISIBLE
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
        rvChat.itemAnimator = null
        rvChat.adapter = this@ChatActivity.adapter
        rvChat.addOnScrollListener(onScrollListener)
        rvChat.addOnLayoutChangeListener(onLayoutChangeListener)
    }

    private fun setAdapter(): ChatAdapter {
        return ChatAdapter(
            onLongClickMessage = { tpMessage, position ->
                viewModel.setLongClickPosition(position)
                viewModel.setClickedTPMessage(tpMessage)
                EmojiBottomSheet(
                    isMe = ChannelObject.userId == tpMessage.userId,
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
        )
    }

    private fun loadMessages(newTPMessages: List<TPMessage>) {
        adapter.addMessages(newTPMessages)
        if (viewModel.isFirstLoad) {
            viewModel.setFirstLoad(false)
            binding.rvChat.scrollToPosition(adapter.itemCount-1)
        }
    }

    private fun sendMessage(tpMessage: TPMessage) {
        binding.layoutChatEmpty.root.visibility = View.GONE
        adapter.addMessage(tpMessage)
        binding.rvChat.scrollToPosition(adapter.itemCount-1)
    }

    private fun receiveMessage(tpMessage: TPMessage) {
        adapter.addMessage(tpMessage)
        viewModel.setMyLastMessage(false)
        val layoutManager = binding.rvChat.layoutManager as LinearLayoutManager
        if (layoutManager.findFirstVisibleItemPosition() == BOTTOM) {
            binding.rvChat.scrollToPosition(adapter.itemCount-1)
        }
    }

    private fun updateReaction(tpMessage: TPMessage) {
        adapter.updateReaction(viewModel.longClickPosition, tpMessage)
    }

    override fun onDestroy() {
        super.onDestroy()
        TalkPlus.removeChannelListener(ChannelObject.tpChannel.channelId)
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

    override fun selectedEmoji(emoji: String) {
        viewModel.updateReaction(emoji)
    }
}