package com.cesoft.cesnostr.follow.vmi

sealed class FollowSideEffect {
    data object Start: FollowSideEffect()
    data object Close: FollowSideEffect()
    data class GoAuthor(val npub: String): FollowSideEffect()
}

