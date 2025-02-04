package com.neptune.klat_uikit_android.core.extension

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.neptune.klat_uikit_android.R

fun ImageView.loadThumbnail(url: String) {
    if (url == "") {
        loadThumbnail(R.drawable.ic_48_default_logo)
        return
    }

    Glide.with(this)
        .load(url)
        .transform(CenterCrop())
        .into(this)
}

fun ImageView.loadThumbnailContainRadius(
    url: String,
    radius: Int
) {
    if (url == "") {
        loadThumbnail(R.drawable.ic_48_default_logo)
        return
    }

    Glide.with(this)
        .load(url)
        .transform(CenterCrop())
        .apply(RequestOptions.bitmapTransform(RoundedCorners(radius)))
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

fun ImageView.loadThumbnailFitCenter(url: String) {
    Glide.with(this)
        .load(url)
        .fitCenter()
        .into(this)
}