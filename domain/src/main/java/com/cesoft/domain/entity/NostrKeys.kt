package com.cesoft.domain.entity

data class NostrKeys(
    val publicKey: NostrPublicKey,
    val privateKey: NostrPrivateKey,
) {
    companion object {
        val Empty = NostrKeys(NostrPublicKey(), NostrPrivateKey())
    }
}