package com.neptune.klat_uikit_android

import android.app.Activity
import android.content.Intent
import com.neptune.klat_uikit_android.feature.channel.list.ChannelListActivity

object KlatUiKit {
    fun start(activity: Activity) {
        val intent = Intent(activity, ChannelListActivity::class.java)
        activity.startActivity(intent)
    }
}