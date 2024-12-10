package com.neptune.klat_uikit_android.feature.channel.create

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.databinding.ActivityChannelCreateBinding

class ChannelCreateActivity : AppCompatActivity(), PhotoActionListener {
    private val viewModel: ChannelCreateViewModel by viewModels()
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
        val bottomSheet = PhotoOptionsBottomSheet(this)
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
    }

    override fun onPhotoCaptured(fileUri: Uri) {
        binding.ivCreateChannelLogo.loadThumbnail(fileUri)
    }

    override fun onPhotoSelected(fileUri: Uri) {

    }
}