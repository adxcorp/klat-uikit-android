package com.neptune.klat_uikit_android.feature.channel.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.feature.channel.list.ChannelListFragment
import io.talkplus.TalkPlus
import io.talkplus.entity.channel.TPChannel
import io.talkplus.entity.channel.TPMessage

class ChannelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_channel_list, ChannelListFragment())
            .commit()
    }
}