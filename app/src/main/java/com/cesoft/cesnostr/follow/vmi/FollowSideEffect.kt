package com.cesoft.cesnostr.follow.vmi

sealed class FollowSideEffect {
    data object Start: FollowSideEffect()
    data object Close: FollowSideEffect()
    //data class AddAuthor(val npub: String): HomeSideEffect()
}

