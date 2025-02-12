package com.neptune.klat_uikit_android

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.feature.channel.main.ChannelActivity
import com.neptune.klat_uikit_android.feature.chat.ChatActivity
import com.neptune.klat_uikit_android.model.LoginRequest
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.user.TPUser
import io.talkplus.params.TPLoginParams
import java.lang.Exception

object KlatUiKit {
    fun initialize(context: Context, appId: String) {
        TalkPlus.init(context, appId)
        ChannelObject.tag = appId
    }

    fun connect(
        activity: Activity,
        loginRequest: LoginRequest,
        oneToOneChannelId: String? = null
    ) {
        val loginType = loginRequest.userToken?.let { TPLoginParams.LoginType.TOKEN } ?: TPLoginParams.LoginType.ANONYMOUS

        val tpLoginParams = TPLoginParams.Builder(loginRequest.userId, loginType).apply {
            if (loginType == TPLoginParams.LoginType.TOKEN) { setLoginToken(loginRequest.userToken) }
            setUserName(loginRequest.userNickname)
            setProfileImageUrl(loginRequest.profileImage)
        }.build()

        TalkPlus.login(tpLoginParams, object : TalkPlus.CallbackListener<TPUser> {
            override fun onSuccess(tpUser: TPUser) {
                ChannelObject.userId = tpUser.userId
                oneToOneChannelId?.let { channelId ->
                    getChannel(channelId, activity)
                } ?: run {
                    val intent = Intent(activity, ChannelActivity::class.java)
                    activity.startActivity(intent)
                }
            }

            override fun onFailure(errorCode: Int, exception: Exception) {
                Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getChannel(
        channelId: String,
        activity: Activity
    ) {
        TalkPlus.getChannel(channelId, object : TalkPlus.CallbackListener<TPChannel> {
            override fun onSuccess(tpChannel: TPChannel) {
                ChannelObject.setTPChannel(tpChannel)
                val intent = Intent(activity, ChatActivity::class.java)
                activity.startActivity(intent)
            }

            override fun onFailure(errorCode: Int, exception: Exception) {
                Toast.makeText(activity, exception.message.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}