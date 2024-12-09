package com.neptune.klat_uikit_android.feature.channel.create

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.databinding.ActivityChannelCreateBinding

class ChannelCreateActivity : AppCompatActivity() {
    private val binding: ActivityChannelCreateBinding by lazy { ActivityChannelCreateBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}