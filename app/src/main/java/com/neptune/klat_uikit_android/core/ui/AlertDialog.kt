package com.neptune.klat_uikit_android.core.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.neptune.klat_uikit_android.databinding.LayoutAlertDialogBinding

class AlertDialog(
//    private val AlertType
) : DialogFragment() {
    private var _binding: LayoutAlertDialogBinding? = null
    private val binding get() = _binding ?: error("LayoutAlertDialogBinding 초기화 에러")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDisplayMetrics()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutAlertDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun setDisplayMetrics() {
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                (resources.displayMetrics.widthPixels * 0.78).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}