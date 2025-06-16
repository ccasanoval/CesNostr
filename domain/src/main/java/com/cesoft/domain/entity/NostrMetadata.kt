package com.cesoft.domain.entity

data class NostrMetadata(
    val npub: String,
    val about: String,
    val name: String,
    val displayName: String,
    val website: String,
    val picture: String,
    val banner: String,
    val lud06: String,//LNURL addres
    val lud16: String,//@ wallet address
    val nip05: String,//PubKey to DNS name
) {
    //public open fun getCustomField(key: kotlin.String): rust.nostr.sdk.JsonValue? { /* compiled code */ }
    companion object {
        val Empty = NostrMetadata("","","","","","","","","", "")
    }
}