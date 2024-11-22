package com.neptune.klat_uikit_android.feature.channel.list

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.databinding.ActivityChannelListBinding

class ChannelListActivity : AppCompatActivity() {
    private val binding: ActivityChannelListBinding by lazy { ActivityChannelListBinding.inflate(layoutInflater) }

    companion object {
        const val EXTRA_USER_TOKEN = "extra_user_token"
        const val EXTRA_USER_ID = "extra_user_ud"
        const val EXTRA_USER_NICKNAME = "extra_user_nickname"
        const val EXTRA_USER_PROFILE_IMAGE = "extra_user_profile_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setUi()
    }

    private fun setUi() = with(binding) {
        layoutHeader.ivLeftBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_back)
            setOnClickListener { finish() }
        }

        layoutHeader.tvLeftText.apply {
            visibility = View.VISIBLE
            text = "채널"
        }

        layoutHeader.ivFirstRightBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_search)
            setOnClickListener {
                // TODO 검색
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
}