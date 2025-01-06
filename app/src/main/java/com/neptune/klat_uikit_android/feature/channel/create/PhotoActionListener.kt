package com.neptune.klat_uikit_android.feature.channel.create

import android.net.Uri

interface PhotoActionListener {
    fun onPhotoCaptured(fileUri: Uri)
    fun onPhotoSelected(fileUri: Uri)
}