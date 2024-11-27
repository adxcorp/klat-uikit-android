package com.neptune.klat_uikit_android.feature.channel.list

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
        setHeaderUI()
    }

    private fun setHeaderUI() = with(binding) {
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
                setSearchUI()
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

    private fun setSearchUI() = with(binding) {
        layoutSearch.root.visibility = View.VISIBLE
        layoutSearch.etSearch.requestFocus()

        // 리팩토링
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(layoutSearch.etSearch, InputMethodManager.SHOW_IMPLICIT)

        layoutSearch.etSearch.addTextChangedListener { input ->
            layoutSearch.tvSearchCancel.isVisible = !input.isNullOrEmpty()
        }

        layoutSearch.tvSearchCancel.setOnClickListener {
            layoutSearch.etSearch.setText("")
            layoutSearch.root.visibility = View.GONE

            // 리팩토링
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(layoutSearch.etSearch.windowToken, 0)
        }
    }
}