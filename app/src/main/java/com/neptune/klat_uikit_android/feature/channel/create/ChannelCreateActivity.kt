package com.neptune.klat_uikit_android.feature.channel.create

import android.content.ContentProvider
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
//            viewModel.createChannel(
//                chanelName = layoutChannelName.etCreateChannelName.text.toString(),
//                memberCount = etCreateMemberCount.text.toString().toInt()
//            )
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
        viewModel.test = getFilePathFromUri(fileUri)
        viewModel.setCurrentUri(fileUri)
    }

    override fun onPhotoSelected(fileUri: Uri) {
        binding.ivCreateChannelLogo.loadThumbnail(fileUri)
        viewModel.test = getFilePathFromUri(fileUri)
        viewModel.setCurrentUri(fileUri)
    }

    private fun getFilePathFromUri(uri: Uri): String {
        val projection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        throw IllegalArgumentException("Cannot find file path for URI: $uri")
    }
}