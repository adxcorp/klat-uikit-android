package com.neptune.klat_uikit_android.feature.chat.emoji

interface OnEmojiBottomSheetListener {
    fun selectedEmoji(emoji: String)
    fun selectedCopyText()
    fun selectedDeleteText()
}