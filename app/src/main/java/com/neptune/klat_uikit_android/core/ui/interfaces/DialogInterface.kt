package com.neptune.klat_uikit_android.core.ui.interfaces

interface DialogInterface {
    fun peerMuteUser()
    fun unPeerMuteUser()
    fun muteUser()
    fun unMuteUser()
    fun banUser(banId: String)
    fun grantOwner()
}