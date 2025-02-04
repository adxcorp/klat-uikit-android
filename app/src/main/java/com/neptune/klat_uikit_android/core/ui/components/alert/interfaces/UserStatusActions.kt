package com.neptune.klat_uikit_android.core.ui.components.alert.interfaces

interface UserStatusActions {
    fun peerMuteUser() { }
    fun muteUser() { }
    fun banUser(banId: String) { }
    fun grantOwner(ownerId: String) { }
}