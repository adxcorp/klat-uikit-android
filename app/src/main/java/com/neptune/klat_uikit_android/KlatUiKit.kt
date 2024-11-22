package com.neptune.klat_uikit_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.neptune.klat_uikit_android.feature.channel.list.ChannelListActivity
import com.neptune.klat_uikit_android.model.LoginRequest
import io.talkplus.TalkPlus

object KlatUiKit {
    fun initialize(context: Context, appId: String) {
        TalkPlus.init(context, appId)
    }

    fun connect(activity: Activity, loginRequest: LoginRequest) {
        val intent = Intent(activity, ChannelListActivity::class.java).apply {
            putExtra(ChannelListActivity.EXTRA_USER_TOKEN, loginRequest.userToken)
            putExtra(ChannelListActivity.EXTRA_USER_ID, loginRequest.userId)
            putExtra(ChannelListActivity.EXTRA_USER_NICKNAME, loginRequest.userNickname)
            putExtra(ChannelListActivity.EXTRA_USER_PROFILE_IMAGE, loginRequest.profileImage)
        }
        activity.startActivity(intent)
    }
}