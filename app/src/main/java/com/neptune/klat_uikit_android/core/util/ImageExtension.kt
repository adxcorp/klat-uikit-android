package com.neptune.klat_uikit_android.core.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop

fun ImageView.loadThumbnail(url: String) {
    Glide.with(this)
        .load(url)
        .transform(CenterCrop())
        .into(this)
}

fun ImageView.loadThumbnail(drawable: Int) {
    Glide.with(this)
        .load(drawable)
        .into(this)
}