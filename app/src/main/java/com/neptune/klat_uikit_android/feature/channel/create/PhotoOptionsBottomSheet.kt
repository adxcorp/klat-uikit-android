package com.neptune.klat_uikit_android.feature.channel.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neptune.klat_uikit_android.databinding.LayoutPhotoOptionsBottomSheetBinding

class PhotoOptionsBottomSheet : BottomSheetDialogFragment() {
    private var _binding: LayoutPhotoOptionsBottomSheetBinding? = null
    private val binding get() = _binding ?: error("LayoutPhotoOptionsBottomSheetBinding 초기화 에러")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutPhotoOptionsBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() = with(binding) {
        binding.clPhotoPick.setOnClickListener {
            dismiss()
        }

        binding.clTakePicture.setOnClickListener {
            dismiss()
        }
    }
}