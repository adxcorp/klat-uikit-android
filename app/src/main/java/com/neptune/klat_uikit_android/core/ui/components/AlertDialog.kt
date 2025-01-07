package com.neptune.klat_uikit_android.core.ui.components

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.ui.components.enums.AlertType
import com.neptune.klat_uikit_android.core.ui.interfaces.DialogInterface
import com.neptune.klat_uikit_android.databinding.LayoutAlertDialogBinding

class AlertDialog(
    private val alertType: AlertType,
    private val userId: String,
    private val userNickname: String,
    private val dialogInterface: DialogInterface
) : DialogFragment() {
    private var _binding: LayoutAlertDialogBinding? = null
    private val binding get() = _binding ?: error("LayoutAlertDialogBinding 초기화 에러")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDisplayMetrics()
        bindView()
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

    private fun bindView() {
        setClickListener()
        setAlertViewType()
    }

    private fun setAlertViewType() = with(binding) {
        when (alertType) {
            AlertType.BAN -> {
                tvAlertRight.setOnClickListener {  }
                setContent(
                    titleDescription = getString(R.string.alert_ban_title, userNickname),
                    contentDescription = getString(R.string.alert_ban_description)
                )
            }

            AlertType.OWNER -> {
                tvAlertRight.setOnClickListener {  }
                setContent(
                    titleDescription = getString(R.string.alert_grant_owner_title, userNickname),
                    contentDescription = getString(R.string.alert_grant_owner_description)
                )
            }

            AlertType.MUTE -> {
                tvAlertRight.setOnClickListener {  }
                setContent(
                    titleDescription = getString(R.string.alert_mute_title, userNickname),
                    contentDescription = getString(R.string.alert_mute_description)
                )
            }
        }
    }

    private fun setClickListener() = with(binding) {
        tvAlertLeft.setOnClickListener { dialog?.dismiss() }
    }

    private fun setContent(
        titleDescription: String,
        contentDescription: String
    ) = with(binding) {
        tvAlertTitle.text = titleDescription
        tvAlertDescription.text = contentDescription
    }
}