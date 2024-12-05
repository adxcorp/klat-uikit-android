package com.neptune.klat_uikit_android.core.util

import android.util.Log

object LogUtils {
    private const val MAX_LOG_LENGTH = 4000

    fun log(tag: String, message: String) {
        if (message.length > MAX_LOG_LENGTH) {
            var start = 0
            while (start < message.length) {
                val end = (start + MAX_LOG_LENGTH).coerceAtMost(message.length)
                Log.d(tag, message.substring(start, end))
                start = end
            }
        } else {
            Log.d(tag, message)
        }
    }
}
