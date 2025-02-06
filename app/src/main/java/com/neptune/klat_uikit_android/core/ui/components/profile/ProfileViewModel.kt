package com.neptune.klat_uikit_android.core.ui.components.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neptune.klat_uikit_android.core.base.BaseUiState
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.data.model.base.Result
import com.neptune.klat_uikit_android.core.data.repository.channel.ChannelRepository
import com.neptune.klat_uikit_android.core.data.repository.member.MemberRepository
import com.neptune.klat_uikit_android.core.data.repository.user.UserEventRepository
import com.neptune.klat_uikit_android.feature.channel.create.CreateChannelUiState
import io.talkplus.entity.channel.TPChannel
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

    private var _profileUiState = MutableSharedFlow<ProfileUiState>()
    val profileUiState: SharedFlow<ProfileUiState>
        get() = _profileUiState.asSharedFlow()

    fun checkSameChatRoom(targetUserId: String) {
        val sortedUserIds: List<String> = listOf(targetUserId, ChannelObject.userId).sorted().map { it.lowercase() }
        val subcategory: String = "${sortedUserIds[0]}_${sortedUserIds[1]}"
        val sameChannel: TPChannel? = ChannelObject.tpChannels.find { it.subCategory == subcategory }

        sameChannel?.let { tpChannel ->
            viewModelScope.launch {
                ChannelObject.setTPChannel(tpChannel)
                _profileUiState.emit(ProfileUiState.MoveChatRoom)
            }
        } ?: createChannel(subcategory, targetUserId)
    }

    private fun createChannel(subcategory: String, targetUserId: String) {
        viewModelScope.launch {
            channelRepository.createChannel(
                memberCount = 2,
                channelName = subcategory.replace("_", ", "),
                subcategory = subcategory,
                category = "klat_uikit_private_chat",
                targetIds = listOf(ChannelObject.userId, targetUserId)
            ).collect { callbackResult ->
                when (callbackResult) {
                    is Result.Success -> _profileUiState.emit(ProfileUiState.MoveChatRoom)
                    is Result.Failure -> { }
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