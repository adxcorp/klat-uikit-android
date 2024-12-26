package com.neptune.klat_uikit_android.core.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    private lateinit var _binding: T
    val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingFactory()
        initViews()
        setContentView(binding.root)
    }

    abstract fun bindingFactory(): T
    abstract fun initViews()
}