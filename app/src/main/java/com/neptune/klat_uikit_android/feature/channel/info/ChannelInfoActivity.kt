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
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.ui.components.alert.AlertDialog
import com.neptune.klat_uikit_android.core.ui.components.enums.StateType
import com.neptune.klat_uikit_android.core.ui.interfaces.ChannelActions
import com.neptune.klat_uikit_android.databinding.ActivityChannelInfoBinding
import com.neptune.klat_uikit_android.feature.member.list.MemberActivity
import kotlinx.coroutines.launch

class ChannelInfoActivity : BaseActivity<ActivityChannelInfoBinding>(), ChannelActions {
    private val viewModel: ChannelInfoViewModel by viewModels()

    override fun bindingFactory(): ActivityChannelInfoBinding {
        return ActivityChannelInfoBinding.inflate(layoutInflater)
    }

    override fun init() {
        bindView()
        observeChannelInfo()
        setListener()
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

            }
            is ChannelInfoUiState.LeaveChannel -> {
                Log.d("!! : ", "leave")
                finish()
            }
            is ChannelInfoUiState.Frozen -> binding.layoutChannelInfo4.switchInfo.isSelected = true
            is ChannelInfoUiState.UnFrozen -> binding.layoutChannelInfo4.switchInfo.isSelected = false
            is ChannelInfoUiState.RemoveChannel -> {
                Log.d("!! : ", "remove")
                finish()
            }
            is ChannelInfoUiState.EnablePush -> binding.layoutChannelInfo5.switchInfo.isSelected = true
            is ChannelInfoUiState.DisablePush -> binding.layoutChannelInfo5.switchInfo.isSelected = false
        }
    }

    override fun onResume() {
        super.onResume()
        setChannelInfo()
    }

    private fun setDefaultUI() = with(binding) {
        layoutChannelInfo1.tvInfoTitle.text = "참여자 목록"
        layoutChannelInfo1.root.setOnClickListener {
            val intent = Intent(this@ChannelInfoActivity, MemberActivity::class.java).apply {
                putExtra(MemberActivity.EXTRA_MEMBER, true)
            }
            startActivity(intent)
        }

        layoutChannelInfo2.root.visibility = View.GONE
        layoutChannelInfo3.root.visibility = View.GONE
        layoutChannelInfo4.root.visibility = View.GONE
    }

    private fun setOwnerUI() = with(binding) {
        layoutChannelInfo1.tvInfoTitle.text = "채널 정보 변경"
        layoutChannelInfo1.root.setOnClickListener {

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
                putExtra(MemberActivity.EXTRA_IS_OWNER, ChannelObject.tpChannel.channelOwnerId == ChannelObject.userId)
            }
            startActivity(intent)
        }

        layoutChannelInfo4.tvInfoSwitchTitle.text = "채널 얼리기"
        layoutChannelInfo4.switchInfo.isChecked = ChannelObject.tpChannel.isFrozen
        layoutChannelInfo4.tvInfoSwitchSubTitle.visibility = View.GONE
    }

    private fun setChannelInfo() = with(binding) {
        ivChannelInfoThumbnail.loadThumbnail(ChannelObject.tpChannel.imageUrl)
        tvChannelInfoTitle.text = ChannelObject.tpChannel.channelName
        tvChannelInfoMemberCount.text = "${ChannelObject.tpChannel.memberCount}명 참여중"
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
        layoutChannelInfo5.switchInfo.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> viewModel.disablePush()
                false -> viewModel.enablePush()
            }
        }
    }

    private fun bindView() = with(binding) {
        if (viewModel.isChannelOwner) setOwnerUI() else setDefaultUI()

        layoutChannelInfo5.tvInfoSwitchTitle.text = "푸시 알림 설정"
        layoutChannelInfo5.tvInfoSwitchSubTitle.text = "이 채널에만 해당하는 설정입니다."
        layoutChannelInfo5.switchInfo.isChecked = ChannelObject.tpChannel.isPushNotificationDisabled

        layoutChannelInfo6.tvInfoTitle.text = if (viewModel.isChannelOwner) "채널 삭제" else "채널 나가기"
        layoutChannelInfo6.ivChannelInfoArrow.visibility = View.GONE
        layoutChannelInfo6.tvInfoTitle.setTextColor(Color.parseColor("#F53D3D"))
    }

    override fun removeChannel() {
        viewModel.removeChannel()
    }

    override fun leaveChannel() {
        viewModel.leaveChannel()
    }
}