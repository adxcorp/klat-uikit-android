package com.neptune.klat_uikit_android.feature.channel.info

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.activity.viewModels
import com.neptune.klat_uikit_android.core.base.BaseActivity
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ActivityChannelInfoBinding
import com.neptune.klat_uikit_android.feature.member.list.MemberActivity

class ChannelInfoActivity : BaseActivity<ActivityChannelInfoBinding>() {
    private val viewModel: ChannelInfoViewModel by viewModels()

    override fun bindingFactory(): ActivityChannelInfoBinding {
        return ActivityChannelInfoBinding.inflate(layoutInflater)
    }

    override fun init() {
        if (ChannelObject.tpChannel.channelOwnerId == ChannelObject.userId) setOwnerUI() else setDefaultUI()
        setListener()
    }

    override fun getExtra() {

    }

    private fun setDefaultUI() = with(binding) {
        setChannelInfo()
        layoutChannelInfo2.root.visibility = View.GONE
        layoutChannelInfo3.root.visibility = View.GONE
        layoutChannelInfo4.root.visibility = View.GONE

        layoutChannelInfo1.tvInfoTitle.text = "참여자 목록"
        layoutChannelInfo1.root.setOnClickListener {
            val intent = Intent(this@ChannelInfoActivity, MemberActivity::class.java).apply {
                putExtra(MemberActivity.EXTRA_MEMBER, true)
            }
            startActivity(intent)
        }

        layoutChannelInfo5.tvInfoSwitchTitle.text = "푸시 알림 설정"
        layoutChannelInfo5.tvInfoSwitchSubTitle.text = "이 채널에만 해당하는 설정입니다."

        layoutChannelInfo6.tvInfoTitle.text = "채널 삭제"
        layoutChannelInfo6.ivChannelInfoArrow.visibility = View.GONE
        layoutChannelInfo6.tvInfoTitle.setTextColor(Color.parseColor("#F53D3D"))
        layoutChannelInfo6.root.setOnClickListener {

        }
    }

    private fun setOwnerUI() = with(binding) {
        setChannelInfo()

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
                putExtra(MemberActivity.EXTRA_IS_OWNER, ChannelObject.ownerId == ChannelObject.userId)
            }
            startActivity(intent)
        }

        layoutChannelInfo4.tvInfoSwitchTitle.text = "채널 얼리기"
        layoutChannelInfo4.tvInfoSwitchSubTitle.visibility = View.GONE

        layoutChannelInfo5.tvInfoSwitchTitle.text = "푸시 알림 설정"
        layoutChannelInfo5.tvInfoSwitchSubTitle.text = "이 채널에만 해당하는 설정입니다."

        layoutChannelInfo6.tvInfoTitle.text = "채널 삭제"
        layoutChannelInfo6.ivChannelInfoArrow.visibility = View.GONE
        layoutChannelInfo6.tvInfoTitle.setTextColor(Color.parseColor("#F53D3D"))
        layoutChannelInfo6.root.setOnClickListener {

        }
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
    }

    companion object {
        const val EXTRA_TP_CHANNEL = "extra_tp_channel"
    }
}