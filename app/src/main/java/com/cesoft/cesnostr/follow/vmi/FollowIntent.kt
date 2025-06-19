package com.cesoft.cesnostr.follow.vmi

import com.adidas.mvi.Intent

sealed class FollowIntent: Intent {
    data object Close: FollowIntent()
    data object Load: FollowIntent()
    data object Reload: FollowIntent()
    //data class AddAuthor(val npub: String): FollowIntent()
}