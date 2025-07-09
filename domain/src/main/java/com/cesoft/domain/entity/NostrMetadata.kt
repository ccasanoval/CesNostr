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

//--------------------------------------------------------------------------------------------------
val bitcoinMetadata = NostrMetadata(
    npub = "npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv",
    about = "Bitcoin is an open source censorship-resistant peer-to-peer immutable network. Trackable digital gold. Don’t trust; verify. Not your keys; not your coins.",
    name = "bitcoin",
    displayName = "Bitcoin",
    website = "https://bitcoin.org/bitcoin.pdf",
    picture = "https://blossom.primal.net/7e830d7297998db82e8e0ba409639c8581f85e99d7649208100879d84ac537d3.jpg",
    banner = "https://blossom.primal.net/74b6896813c18781d6edc1c00472a1c7121903424a7d99606c8f99709a41e2c9.jpg",
    lud06 = "",
    lud16 = "bd7ea219a0929a84@coinos.io",
    nip05 = "Bitcoin@NostrVerified.com"
)
val opusMetadata = NostrMetadata(
    npub="npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k",
    about="Cyberpunk writer and creator",
    name="Opus2501",
    displayName="Opus2501",
    website="https://cortados.freevar.com",
    picture="https://cortados.freevar.com/web/front/images/scorpion_s.png",
    banner="https://cortados.freevar.com/web/front/images/logo.png",
    lud06="",
    lud16="wordybritish81@walletofsatoshi.com",
    nip05=""
)
