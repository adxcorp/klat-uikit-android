package com.neptune.klat_uikit_android.core.ui.interfaces

interface DialogInterface {
    fun peerMuteUser()
    fun muteUser()
    fun banUser(banId: String)
    fun grantOwner(ownerId: String)
}