package com.neptune.klat_uikit_android.feature.chat.photo

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.ChannelObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

class PhotoDetailViewModel : ViewModel() {
    private var _photoUiState = MutableSharedFlow<PhotoDetailUiState>()
    val photoUiState: SharedFlow<PhotoDetailUiState>
        get() = _photoUiState.asSharedFlow()


    @SuppressLint("SimpleDateFormat")
    fun downloadImage(downloadManager: DownloadManager) {
        val fileName: String = "/klat/${SimpleDateFormat("yyyyMMddHHmmss").format(Date())}.jpg"
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(ChannelObject.tpMessage.fileUrl)).apply {
            setTitle(fileName)
        }.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setMimeType("image/*")
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES,
                fileName
            )
        downloadManager.enqueue(request).let { downloadId ->
            checkDownloadStatus(downloadManager, downloadId)
        }
    }

    @SuppressLint("Range")
    fun checkDownloadStatus(
        downloadManager: DownloadManager,
        downloadId: Long
    ) {
        viewModelScope.launch {
            val query = DownloadManager.Query().setFilterById(downloadId)
            var cursor: Cursor = downloadManager.query(query)

            while (cursor.moveToFirst()) {
                try {
                    cursor.use {
                        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                _photoUiState.emit(PhotoDetailUiState.DownloadSuccess)
                                return@launch
                            }

                            DownloadManager.STATUS_FAILED -> {
                                _photoUiState.emit(PhotoDetailUiState.DownloadFailed)
                                return@launch
                            }

                            DownloadManager.STATUS_RUNNING -> _photoUiState.emit(PhotoDetailUiState.Downloading)
                        }
                    }
                    delay(500)
                    cursor = downloadManager.query(query)
                } catch (exception: Exception) {
                    _photoUiState.emit(PhotoDetailUiState.Exception)
                    return@launch
                }
            }
        }
    }
}