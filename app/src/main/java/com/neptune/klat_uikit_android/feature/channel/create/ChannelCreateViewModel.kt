package com.neptune.klat_uikit_android.feature.channel.create

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import java.io.File

class ChannelCreateViewModel : ViewModel() {
    private var currentUri: Uri? = null

    fun setCurrentUri(uri: Uri) {
        currentUri = uri
    }

    var test = ""

    fun createChannel(
        chanelName: String,
        memberCount: Int,
        channelType: String = if (memberCount == SUPER_TYPE) "super_private" else "private",
        imageUrl: String
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("test1", "test2")

        TalkPlus.createChannel(
            listOf("test1", "test2"),
            null,
            false,
            memberCount,
            false,
            channelType,
            chanelName,
            "",
            "",
            "",
            imageUrl,
            jsonObject,
            File(test),
            object : TalkPlus.CallbackListener<TPChannel> {
                override fun onSuccess(tpChannel: TPChannel) {
                    Log.d("!! : 성공", tpChannel.toString())
                }
                override fun onFailure(i: Int, e: Exception) {

                }
            }
        )
    }

    companion object {
        private const val SUPER_TYPE = 100
    }
}