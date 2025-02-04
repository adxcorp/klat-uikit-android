package com.neptune.klat_uikit_android.feature.chat.photo

import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.BaseActivity
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.loadThumbnail
import com.neptune.klat_uikit_android.core.extension.loadThumbnailFitCenter
import com.neptune.klat_uikit_android.databinding.ActivityPhotoDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class PhotoDetailActivity : BaseActivity<ActivityPhotoDetailBinding>() {
    companion object {
        private const val INVALID_TIME = "1900. 01. 01. 00:00"
    }

    private val viewModel: PhotoDetailViewModel by viewModels()

    override fun bindingFactory(): ActivityPhotoDetailBinding {
        return ActivityPhotoDetailBinding.inflate(layoutInflater)
    }

    override fun init() {
        setHeaderUI()
        bindView()
        observePhotoDetailUiState()
    }

    private fun observePhotoDetailUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.photoUiState.collect { photoDetailUiState ->
                    handleUiState(photoDetailUiState)
                }
            }
        }
    }

    private fun handleUiState(photoDetailUiState: PhotoDetailUiState) {
        when (photoDetailUiState) {
            PhotoDetailUiState.Downloading -> showToast("다운로드중..")
            PhotoDetailUiState.DownloadFailed -> showToast("다운로드에 실패했습니다.")
            PhotoDetailUiState.DownloadSuccess -> showToast("앨범에 저장되었습니다.")
            PhotoDetailUiState.Exception -> showToast("예외발생")
            PhotoDetailUiState.None -> Unit
        }
    }

    private fun setHeaderUI() = with(binding.layoutPhotoDetailHeader) {
        ivLeftBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_white_back)
            setOnClickListener { finish() }
        }

        ivSecondRightBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_white_download)
            setOnClickListener {
                if (true) {
                    viewModel.downloadImage(getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
                }
            }
        }

        tvMidText.apply {
            visibility = View.VISIBLE
            text = ChannelObject.tpMessage.username
            setTextColor(Color.WHITE)
        }

        tvMidSubText.apply {
            visibility = View.VISIBLE
            text = longToTime(ChannelObject.tpMessage.createdAt)
            setTextColor(Color.parseColor("#999999"))
        }
    }

    private fun bindView() = with(binding) {
        ivPhotoDetail.loadThumbnailFitCenter(ChannelObject.tpMessage.fileUrl)
    }

    private fun longToTime(createdAt: Long?): String {
        return createdAt?.let { SimpleDateFormat("yyyy. MM. dd. HH:mm", Locale.getDefault()).format(it) } ?: INVALID_TIME
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}