package com.neptune.klat_uikit_android.feature.channel.create

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.databinding.ActivityChannelCreateBinding

class ChannelCreateActivity : AppCompatActivity() {
    private val binding: ActivityChannelCreateBinding by lazy { ActivityChannelCreateBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        ivCreateChannelClose.setOnClickListener {
            finish()
        }

        tvCreateChannel.setOnClickListener {

        }

        ivCreateChannelLogo.setOnClickListener {
            showPhotoOptionsBottomSheet()
        }
    }

    private fun showPhotoOptionsBottomSheet() {
        val bottomSheet = PhotoOptionsBottomSheet().apply {

        }
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }
}