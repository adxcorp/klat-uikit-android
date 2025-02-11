package com.neptune.klat_uikit_android.core.ui.components.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import com.neptune.klat_uikit_android.core.data.repository.member.MemberRepository
import com.neptune.klat_uikit_android.core.data.repository.user.UserEventRepository
import io.talkplus.entity.user.TPUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userEventRepository: UserEventRepository = UserEventRepository(),
    private val memberRepository: MemberRepository = MemberRepository(),
    private val channelRepository: ChannelRepository = ChannelRepository()
) : ViewModel() {
    private var lastUser: TPUser? = null
    val peerMutedUsers = arrayListOf<TPUser>()

    var isMuted: Boolean = false
        private set

    private var userId: String = ""

    val isMyProfile: Boolean
        get() = userId == ChannelObject.userId

    val isChannelOwner: Boolean = ChannelObject.userId == ChannelObject.tpChannel.channelOwnerId

    var targetUserId: String = ""
        private set

    private var _profileUiState = MutableSharedFlow<ProfileUiState>()
    val profileUiState: SharedFlow<ProfileUiState>
        get() = _profileUiState.asSharedFlow()

    fun joinInvitationChannel(targetUserId: String) {
        val sortedUserIds: List<String> = listOf(targetUserId, ChannelObject.userId).sorted().map { it.lowercase() }
        val channelName: String = "${sortedUserIds[0]},${sortedUserIds[1]}"
        val channelId: String = "klat_uikit_invitation_${sortedUserIds[0]}_${sortedUserIds[1]}"
        val invitationCode: String = "${sortedUserIds[0]}_${sortedUserIds[1]}"

        viewModelScope.launch {
            channelRepository.joinChannel(
                channelId = channelId,
                invitationCode = invitationCode
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> if (callbackResult.successData.memberCount < 2) addMember(targetUserId)
                    is Result.Failure -> {
                        when (callbackResult.failResult.errorCode) {
                            2003 -> createChannel(
                                channelName = channelName,
                                targetUserId = targetUserId,
                                channelId = channelId,
                                invitationCode = invitationCode
                            )
                            else -> _profileUiState.emit(ProfileUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                        }
                    }
                }
            }
        }
    }

    private fun addMember(targetUserId: String) {
        this.targetUserId = targetUserId
        viewModelScope.launch {
            channelRepository.addMember(targetUserId).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _profileUiState.emit(ProfileUiState.AddMember)
                    is Result.Failure -> _profileUiState.emit(ProfileUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    private fun createChannel(channelName: String, targetUserId: String, channelId: String, invitationCode: String) {
        viewModelScope.launch {
            channelRepository.createChannel(
                memberCount = 2,
                channelName = channelName,
                invitationCode = invitationCode,
                targetIds = listOf(ChannelObject.userId, targetUserId),
                channelId = channelId
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _profileUiState.emit(ProfileUiState.CreateOneToOneChatRoom)
                    is Result.Failure -> _profileUiState.emit(ProfileUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }

    }

    fun getPeerMutedUsers() {
        viewModelScope.launch {
            memberRepository.getPeerMutedMembers(lastUser).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> {
                        lastUser = callbackResult.successData.tpMembers.lastOrNull()
                        peerMutedUsers.addAll(callbackResult.successData.tpMembers)

                        when (callbackResult.successData.hasNext) {
                            true -> getPeerMutedUsers()
                            false -> _profileUiState.emit(ProfileUiState.GetPeerMutedUsers)
                        }
                    }
                    is Result.Failure -> _profileUiState.emit(ProfileUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun unMuteUser(targetId: String) {
        viewModelScope.launch {
            userEventRepository.unMuteUser(targetId).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _profileUiState.emit(ProfileUiState.UnMuteUser)
                    is Result.Failure -> _profileUiState.emit(ProfileUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun peerUnMuteUser(targetId: String) {
        viewModelScope.launch {
            userEventRepository.peerUnMuteUser(targetId).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _profileUiState.emit(ProfileUiState.PeerUnMuteUser)
                    is Result.Failure -> _profileUiState.emit(ProfileUiState.BaseState(BaseUiState.Error(callbackResult.failResult)))
                }
            }
        }
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun setMute(isMute: Boolean) {
        isMuted = isMute
    }
}