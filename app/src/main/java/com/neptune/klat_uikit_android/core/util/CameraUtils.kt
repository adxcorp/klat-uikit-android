package com.neptune.klat_uikit_android.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import com.neptune.klat_uikit_android.core.base.ChannelObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraUtils {
    fun openCamera(context: ComponentActivity, openCameraLauncher: ActivityResultLauncher<Uri>) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context.packageManager) != null) {
            try {
                createImageFile(context).let { photoFile ->
                    val currentPhotoUri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        photoFile
                    )
                    ChannelObject.setPhotoUri(currentPhotoUri)
                }
                ChannelObject.photoUri?.let { uri ->
                    openCameraLauncher.launch(uri)
                }
            } catch (e: Exception) {
                context.runOnUiThread {
                    Toast.makeText(context, "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
}