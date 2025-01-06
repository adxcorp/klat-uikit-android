package com.neptune.klat_uikit_android.core.extension

import android.content.Context

fun Int.pxToDp(context: Context): Float {
    return this / context.resources.displayMetrics.density
}

fun Int.dpToPx(context: Context): Float {
    return this * context.resources.displayMetrics.density
}

fun Float.pxToDp(context: Context): Float {
    return this / context.resources.displayMetrics.density
}
