package com.neptune.klat_uikit_android.core.extension

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import java.net.URI

fun ImageView.loadThumbnail(url: String) {
    Glide.with(this)
        .load(url)
        .transform(CenterCrop())
        .into(this)
}

fun ImageView.loadThumbnail(uri: Uri) {
    Glide.with(this)
        .load(uri)
        .transform(CenterCrop())
        .into(this)
}

fun ImageView.loadThumbnail(drawable: Int) {
    Glide.with(this)
        .load(drawable)
        .into(this)
}