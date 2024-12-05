package com.neptune.klat_uikit_android.core.extension

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable

fun <T : Serializable> Intent.getSerializableList(key: String): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        @Suppress("UNCHECKED_CAST")
        this.getSerializableExtra(key, ArrayList::class.java) as? ArrayList<T>
    } else {
        @Suppress("UNCHECKED_CAST")
        this.getSerializableExtra(key) as? ArrayList<T>
    }
}

fun <T : Serializable> Bundle.getSerializableList(key: String): ArrayList<T>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        @Suppress("UNCHECKED_CAST")
        this.getSerializable(key, ArrayList::class.java) as? ArrayList<T>
    } else {
        @Suppress("UNCHECKED_CAST")
        this.getSerializable(key) as? ArrayList<T>
    }
}

inline fun <reified T : Serializable> Intent.getSerializable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.getSerializableExtra(key, T::class.java)
    } else {
        this.getSerializableExtra(key) as T
    }
}
