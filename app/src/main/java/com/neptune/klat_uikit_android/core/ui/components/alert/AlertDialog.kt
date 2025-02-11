package com.neptune.klat_uikit_android.core.ui.components.alert

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.extension.showToast
import com.neptune.klat_uikit_android.core.ui.components.enums.StateType
import com.neptune.klat_uikit_android.core.ui.components.alert.interfaces.ChannelActions
import com.neptune.klat_uikit_android.core.ui.components.alert.interfaces.MessageActions
import com.neptune.klat_uikit_android.core.ui.components.alert.interfaces.UserStatusActions
import com.neptune.klat_uikit_android.databinding.LayoutAlertDialogBinding
import kotlinx.coroutines.launch

class AlertDialog(
    private val stateType: StateType,
    private val userId: String = "",
    private val title: String = "",
    private val userStatusActions: UserStatusActions? = null,
    private val channelActions: ChannelActions? = null,
    private val messageActions: MessageActions? = null
) : DialogFragment() {
    private var _binding: LayoutAlertDialogBinding? = null
    private val binding get() = _binding ?: error("LayoutAlertDialogBinding 초기화 에러")

    private val viewModel: AlertViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutAlertDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDisplayMetrics()
        observeAlertUiState()
        setClickListener()
        setAlertViewType()
    }

    private fun observeAlertUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.alertUiState.collect { alertUiState ->
                    handleAlertUiState(alertUiState)
                }
            }
        }
    }

    private fun handleAlertUiState(alertUiState: AlertUiState) {
        when (alertUiState) {
            is AlertUiState.BaseState -> {
                when (alertUiState.baseState) {
                    is BaseUiState.Error -> requireActivity().showToast("${alertUiState.baseState.failedResult.errorCode}")
                    is BaseUiState.Loading -> { }
                    is BaseUiState.LoadingFinish -> { }
                }
            }
            is AlertUiState.BanUser -> userStatusActions?.banUser(userId)
            is AlertUiState.MuteUser -> userStatusActions?.muteUser()
            is AlertUiState.PeerMuteUser -> userStatusActions?.peerMuteUser()
            is AlertUiState.GrantOwner -> userStatusActions?.grantOwner(userId)
            is AlertUiState.LeaveChannel -> channelActions?.leaveChannel()
            is AlertUiState.RemoveChannel -> channelActions?.removeChannel()
        }
        dialog?.dismiss()
    }

    private fun setAlertViewType() = with(binding) {
        when (stateType) {
            StateType.BAN -> {
                tvAlertRight.setOnClickListener { viewModel.banUser(userId)  }
                setContent(
                    titleDescription = getString(R.string.alert_ban_title, title),
                    contentDescription = getString(R.string.alert_ban_description)
                )
            }

            StateType.OWNER -> {
                tvAlertRight.setOnClickListener { viewModel.grantOwner(userId) }
                setContent(
                    titleDescription = getString(R.string.alert_grant_owner_title, title),
                    contentDescription = getString(R.string.alert_grant_owner_description)
                )
            }

            StateType.MUTE -> {
                tvAlertRight.setOnClickListener {
                    when {
                        ChannelObject.tpChannel.channelOwnerId == ChannelObject.userId -> viewModel.muteUser(userId)
                        else -> viewModel.peerMuteUser(userId)
                    }
                }
                setContent(
                    titleDescription = getString(R.string.alert_mute_title, title),
                    contentDescription = getString(R.string.alert_mute_description)
                )
            }

            StateType.REMOVE -> {
                tvAlertRight.setOnClickListener {
                    channelActions?.removeChannel()
                    dialog?.dismiss()
                }
                setContent(
                    titleDescription = getString(R.string.alert_remove_title, title),
                    contentDescription = getString(R.string.alert_remove_description)
                )
            }

            StateType.LEAVE -> {
                tvAlertRight.setOnClickListener {
                    channelActions?.leaveChannel()
                    dialog?.dismiss()
                }
                setContent(
                    titleDescription = getString(R.string.alert_leave_title, title),
                    contentDescription = getString(R.string.alert_leave_description)
                )
            }

            StateType.DELETE_MESSAGE -> {
                tvAlertRight.setOnClickListener {
                    messageActions?.deleteMessage()
                    dialog?.dismiss()
                }
                setContent(
                    titleDescription = getString(R.string.alert_delete_message),
                    contentDescription = getString(R.string.alert_delete_message_description)
                )
            }
        }
    }

    private fun setClickListener() = with(binding) {
        tvAlertLeft.setOnClickListener { dialog?.dismiss() }
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

    private fun setContent(
        titleDescription: String,
        contentDescription: String
    ) = with(binding) {
        tvAlertTitle.text = titleDescription
        tvAlertDescription.text = contentDescription
    }
}