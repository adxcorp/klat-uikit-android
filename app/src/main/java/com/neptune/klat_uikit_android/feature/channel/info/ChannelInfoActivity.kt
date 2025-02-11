package com.neptune.klat_uikit_android.feature.channel.info

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.neptune.klat_uikit_android.core.base.BaseActivity
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.extension.showToast
import com.neptune.klat_uikit_android.core.ui.components.alert.AlertDialog
import com.neptune.klat_uikit_android.core.ui.components.enums.StateType
import com.neptune.klat_uikit_android.core.ui.components.alert.interfaces.ChannelActions
import com.neptune.klat_uikit_android.databinding.ActivityChannelInfoBinding
import com.neptune.klat_uikit_android.feature.channel.create.ChannelCreateActivity
import com.neptune.klat_uikit_android.feature.channel.main.ChannelActivity
import com.neptune.klat_uikit_android.feature.chat.ChatActivity
import com.neptune.klat_uikit_android.feature.member.list.MemberActivity
import kotlinx.coroutines.launch

class ChannelInfoActivity : BaseActivity<ActivityChannelInfoBinding>(), ChannelActions {
    private val viewModel: ChannelInfoViewModel by viewModels()

    override fun bindingFactory(): ActivityChannelInfoBinding {
        return ActivityChannelInfoBinding.inflate(layoutInflater)
    }

    override fun init() {
        setListener()
        observeChannelInfo()
    }

    override fun onResume() {
        super.onResume()
        bindView()
    }

    private fun observeChannelInfo() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.channelInfoUiState.collect { channelInfoUiState ->
                    handleChannelInfoUiState(channelInfoUiState)
                }
            }
        }
    }

    private fun handleChannelInfoUiState(channelInfoUiState: ChannelInfoUiState) {
        when (channelInfoUiState) {
            is ChannelInfoUiState.BaseState -> {
                when (channelInfoUiState.baseState) {
                    is BaseUiState.Error -> showToast("${channelInfoUiState.baseState.failedResult.errorCode}")
                    is BaseUiState.Loading -> binding.pgChannelInfo.visibility = View.VISIBLE
                    is BaseUiState.LoadingFinish -> binding.pgChannelInfo.visibility = View.GONE
                }
            }
            is ChannelInfoUiState.LeaveChannel -> moveChannelListScreen()
            is ChannelInfoUiState.RemoveChannel -> moveChannelListScreen()
            is ChannelInfoUiState.Frozen -> binding.layoutChannelInfo4.switchInfo.isSelected = true
            is ChannelInfoUiState.UnFrozen -> binding.layoutChannelInfo4.switchInfo.isSelected = false
            is ChannelInfoUiState.EnablePush -> binding.layoutChannelInfo5.switchInfo.isSelected = true
            is ChannelInfoUiState.DisablePush -> binding.layoutChannelInfo5.switchInfo.isSelected = false
        }
    }

    private fun setDefaultUI() = with(binding) {
        layoutChannelInfo1.tvInfoTitle.text = "참여자 목록"
        layoutChannelInfo1.root.setOnClickListener {
            val intent = Intent(this@ChannelInfoActivity, MemberActivity::class.java).apply {
                putExtra(MemberActivity.EXTRA_MEMBER, true)
            }
            startActivity(intent)
        }

        layoutChannelInfo2.tvInfoTitle.text = "음소거 목록"
        layoutChannelInfo2.root.setOnClickListener {
            val intent = Intent(this@ChannelInfoActivity, MemberActivity::class.java).apply {
                putExtra(MemberActivity.EXTRA_IS_OWNER, viewModel.isChannelOwner)
            }
            startActivity(intent)
        }

        layoutChannelInfo3.root.visibility = View.GONE
        layoutChannelInfo4.root.visibility = View.GONE
    }

    private fun setOwnerUI() = with(binding) {
        layoutChannelInfo1.tvInfoTitle.text = "채널 정보 변경"
        layoutChannelInfo1.root.setOnClickListener {
            val intent = Intent(this@ChannelInfoActivity, ChannelCreateActivity::class.java).apply {
                putExtra(ChannelCreateActivity.EXTRA_TYPE, ChannelCreateActivity.UPDATE)
            }
            startActivity(intent)
        }

        layoutChannelInfo2.tvInfoTitle.text = "참여자 목록"
        layoutChannelInfo2.root.setOnClickListener {
            val intent = Intent(this@ChannelInfoActivity, MemberActivity::class.java).apply {
                putExtra(MemberActivity.EXTRA_MEMBER, true)
            }
            startActivity(intent)
        }

        layoutChannelInfo3.tvInfoTitle.text = "음소거 목록"
        layoutChannelInfo3.root.setOnClickListener {
            val intent = Intent(this@ChannelInfoActivity, MemberActivity::class.java).apply {
                putExtra(MemberActivity.EXTRA_IS_OWNER, viewModel.isChannelOwner)
            }
            startActivity(intent)
        }

        layoutChannelInfo4.tvInfoSwitchTitle.text = "채널 얼리기"
        layoutChannelInfo4.tvInfoSwitchSubTitle.text = "채널 소유자만 메시지 전송이 가능합니다."
        layoutChannelInfo4.switchInfo.isChecked = ChannelObject.tpChannel.isFrozen
        layoutChannelInfo4.switchInfo.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> viewModel.freezeChannel()
                false -> viewModel.unFreezeChannel()
            }
        }
    }

    private fun setListener() = with(binding) {
        ivChannelInfoBack.setOnClickListener {
            finish()
        }

        // 채널 나가기, 삭제
        layoutChannelInfo6.root.setOnClickListener {
            AlertDialog(
                stateType = if (viewModel.isChannelOwner) StateType.REMOVE else StateType.LEAVE,
                title = ChannelObject.tpChannel.channelName,
                channelActions = this@ChannelInfoActivity
            ).showNow(supportFragmentManager, null)
        }

        // 푸시 설정
        layoutChannelInfo5.switchInfo.setOnClickListener {
            when (ChannelObject.tpChannel.isPushNotificationDisabled) {
                true -> viewModel.enablePush()
                false -> viewModel.disablePush()
            }
        }
    }

    private fun bindView() = with(binding) {
        if (viewModel.isChannelOwner) setOwnerUI() else setDefaultUI()

        ivChannelInfoThumbnail.loadThumbnail(ChannelObject.tpChannel.imageUrl)
        tvChannelInfoTitle.text = ChannelObject.tpChannel.channelName
        tvChannelInfoMemberCount.text = "${ChannelObject.tpChannel.memberCount}명 참여중"

        layoutChannelInfo5.tvInfoSwitchTitle.text = "푸시 알림 설정"
        layoutChannelInfo5.tvInfoSwitchSubTitle.text = "이 채널에 대한 푸시 알림을 수신하지 않습니다."
        layoutChannelInfo5.switchInfo.isChecked = !ChannelObject.tpChannel.isPushNotificationDisabled

        layoutChannelInfo6.tvInfoTitle.text = if (viewModel.isChannelOwner) "채널 삭제" else "채널 나가기"
        layoutChannelInfo6.ivChannelInfoArrow.visibility = View.GONE
        layoutChannelInfo6.tvInfoTitle.setTextColor(Color.parseColor("#F53D3D"))
    }

    private fun moveChannelListScreen() {
        val intent = Intent(this, ChannelActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    override fun removeChannel() {
        viewModel.removeChannel()
    }

    override fun leaveChannel() {
        viewModel.leaveChannel()
    }
}