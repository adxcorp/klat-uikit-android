package com.neptune.klat_uikit_android.feature.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.feature.channel.list.ChannelListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_channel_list, ChannelListFragment())
            .commit()
    }

    companion object {
        const val EXTRA_USER_TOKEN = "extra_user_token"
        const val EXTRA_USER_ID = "extra_user_ud"
        const val EXTRA_USER_NICKNAME = "extra_user_nickname"
        const val EXTRA_USER_PROFILE_IMAGE = "extra_user_profile_image"
    }
}