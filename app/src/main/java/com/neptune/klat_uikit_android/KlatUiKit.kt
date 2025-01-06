package com.neptune.klat_uikit_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.feature.channel.main.ChannelActivity
import com.neptune.klat_uikit_android.feature.channel.search.ChannelSearchActivity
import com.neptune.klat_uikit_android.model.LoginRequest
import io.talkplus.TalkPlus
import io.talkplus.entity.user.TPUser
import io.talkplus.params.TPLoginParams
import java.lang.Exception

object KlatUiKit {
    fun initialize(context: Context, appId: String) {
        TalkPlus.init(context, appId)
        ChannelObject.tag = appId
    }

    fun connect(activity: Activity, loginRequest: LoginRequest) {
        val loginType = loginRequest.userToken?.let { TPLoginParams.LoginType.TOKEN } ?: TPLoginParams.LoginType.ANONYMOUS

        val tpLoginParams = TPLoginParams.Builder(loginRequest.userId, loginType).apply {
            if (loginType == TPLoginParams.LoginType.TOKEN) { setLoginToken(loginRequest.userToken) }
            setUserName(loginRequest.userNickname)
            setProfileImageUrl(loginRequest.profileImage)
        }.build()

        TalkPlus.login(tpLoginParams, object : TalkPlus.CallbackListener<TPUser> {
            override fun onSuccess(tpUser: TPUser) {
                val intent = Intent(activity, ChannelActivity::class.java)
                ChannelObject.userId = tpUser.userId
                activity.startActivity(intent)
            }

            override fun onFailure(errorCode: Int, exception: Exception) {
                Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}