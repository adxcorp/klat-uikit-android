package com.neptune.klat_uikit_android.feature.channel.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.feature.channel.list.ChannelListFragment

class ChannelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_channel_list, ChannelListFragment())
            .commit()
    }
}