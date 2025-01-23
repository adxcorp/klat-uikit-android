package com.neptune.klat_uikit_android.feature.channel.create
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.util.FileUtils
import com.neptune.klat_uikit_android.databinding.ActivityChannelCreateBinding
import com.neptune.klat_uikit_android.feature.chat.ChatActivity
import kotlinx.coroutines.launch

class ChannelCreateActivity : AppCompatActivity(), PhotoActionListener {
    companion object {
        const val EXTRA_TYPE = "extra_type"

        const val CREATE = "create"
        const val UPDATE = "update"
    }

    private val viewModel: ChannelCreateViewModel by viewModels()
    private val binding: ActivityChannelCreateBinding by lazy { ActivityChannelCreateBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
        setClickListener()
        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createChannelUiState.collect { createChannelUiState ->
                    handleUiState(createChannelUiState)
                }
            }
        }
    }

    private fun handleUiState(uiState: CreateChannelUiState) {
        when (uiState) {
            is CreateChannelUiState.CreateChannel -> {
                startActivity(Intent(this, ChatActivity::class.java))
                finish()
            }
            is CreateChannelUiState.UpdateChannel -> finish()
        }
    }

    private fun init() = with(binding) {
        intent.getStringExtra(EXTRA_TYPE)?.let { viewModel.setChannelType(it) } ?: return@with

        when(viewModel.channelType) {
            CREATE -> Unit
            UPDATE -> updateChannelUI()
        }

        layoutChannelName.etCreateChannelName.addTextChangedListener { channelName ->
            layoutChannelName.tvChannelNameCount.text = "${channelName?.length}/20"
        }
    }

    private fun updateChannelUI() = with(binding) {
        tvChannelTitle.text = "채널 정보 변경"
        tvCreateChannel.text = "저장"
        layoutChannelName.etCreateChannelName.setText(ChannelObject.tpChannel.channelName)
        layoutChannelName.tvChannelNameCount.text = "${ChannelObject.tpChannel.channelName.length}/20"
        ivCreateChannelLogo.loadThumbnail(ChannelObject.tpChannel.imageUrl)
        clChannelJoinMember.visibility = View.GONE
    }

    private fun setClickListener() = with(binding) {
        ivCreateChannelClose.setOnClickListener {
            finish()
        }

        tvCreateChannel.setOnClickListener {
            viewModel.apply {
                setChannelName(layoutChannelName.etCreateChannelName.text.toString())
                setMemberCount(etCreateMemberCount.text.toString().toInt())
                upsert()
            }
        }

        ivCreateChannelLogo.setOnClickListener {
            showPhotoOptionsBottomSheet()
        }
    }

    private fun showPhotoOptionsBottomSheet() {
        val bottomSheet = PhotoOptionsBottomSheet(this)
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    override fun onPhotoCaptured(fileUri: Uri) = with(FileUtils) {
        binding.ivCreateChannelLogo.loadThumbnail(fileUri)
        viewModel.setPhotoFile(resizeImage(getFileFromUri(this@ChannelCreateActivity, fileUri)))
    }

    override fun onPhotoSelected(fileUri: Uri) = with(FileUtils) {
        binding.ivCreateChannelLogo.loadThumbnail(fileUri)
        viewModel.setPhotoFile(resizeImage(getFileFromUri(this@ChannelCreateActivity, fileUri)))
    }
}