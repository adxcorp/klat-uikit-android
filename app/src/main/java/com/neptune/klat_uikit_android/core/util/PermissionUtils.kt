package com.neptune.klat_uikit_android.core.util

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionUtils {
    fun showPermissionRationale(
        context: ComponentActivity,
        title: String,
        message: String
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("허용") { _, _ ->
                openAppSettings(context)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    fun checkCameraPermission(
        context: ComponentActivity,
        requestPermissionCameraLauncher: ActivityResultLauncher<String>
    ) {
        val cameraPermission: String = Manifest.permission.CAMERA
        val cameraPermissionState = (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ))
        when (cameraPermissionState) {
            PackageManager.PERMISSION_GRANTED -> requestPermissionCameraLauncher.launch(cameraPermission)
            PackageManager.PERMISSION_DENIED -> requestPermissionCameraLauncher.launch(cameraPermission)
        }
    }

    fun checkGalleryPermission(requestPermissionGalleryLauncher: ActivityResultLauncher<Array<String>>) {
        when {
            // 34 이상
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                requestPermissionGalleryLauncher.launch(arrayOf(
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    Manifest.permission.READ_MEDIA_IMAGES,
                ))
            }

            // 33 이상
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                requestPermissionGalleryLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
            }

            // 33 미만
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                requestPermissionGalleryLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        }
    }

    private fun openAppSettings(context: ComponentActivity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun checkMediaPermissions(permissions: Map<String, Boolean>): Boolean {
        return when {
            // Android 14(API 34) 이상
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissions["android.permission.READ_MEDIA_VISUAL_USER_SELECTED"] == true ||
                        permissions["android.permission.READ_MEDIA_IMAGES"] == true
            }
            // Android 13(API 33)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                permissions["android.permission.READ_MEDIA_IMAGES"] == true
            }
            // Android 10 ~ 12 (API 29 ~ 32)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                permissions["android.permission.READ_EXTERNAL_STORAGE"] == true
            }
            // Android 9 이하 (API 28 이하)
            else -> {
                permissions["android.permission.WRITE_EXTERNAL_STORAGE"] == true
            }
        }
    }
}