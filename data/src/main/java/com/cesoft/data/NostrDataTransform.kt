package com.cesoft.data

import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.entity.NostrMetadata
import rust.nostr.sdk.Event
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.Metadata
import rust.nostr.sdk.MetadataRecord
import rust.nostr.sdk.Tag
import rust.nostr.sdk.Tags
import java.time.LocalDateTime
import java.time.ZoneOffset

/// KindStandard
internal fun KindStandard?.toEntity(): NostrKindStandard = when(this) {
    KindStandard.AUTHENTICATION -> NostrKindStandard.AUTHENTICATION
    KindStandard.APPLICATION_SPECIFIC_DATA -> NostrKindStandard.APPLICATION_SPECIFIC_DATA
    KindStandard.BADGE_DEFINITION -> NostrKindStandard.BADGE_DEFINITION
    KindStandard.BADGE_AWARD -> NostrKindStandard.BADGE_AWARD
    KindStandard.BOOKMARKS -> NostrKindStandard.BOOKMARKS
    KindStandard.BOOKMARK_SET -> NostrKindStandard.BOOKMARK_SET
    KindStandard.CHANNEL_MESSAGE -> NostrKindStandard.CHANNEL_MESSAGE
    KindStandard.CHANNEL_METADATA -> NostrKindStandard.CHANNEL_METADATA
    KindStandard.EMOJIS -> NostrKindStandard.EMOJIS
    KindStandard.FILE_METADATA -> NostrKindStandard.FILE_METADATA
    KindStandard.FOLLOW_SET -> NostrKindStandard.FOLLOW_SET
    KindStandard.GENERIC_REPOST -> NostrKindStandard.GENERIC_REPOST
    KindStandard.GIFT_WRAP -> NostrKindStandard.GIFT_WRAP
    KindStandard.GIT_ISSUE -> NostrKindStandard.GIT_ISSUE
    KindStandard.HTTP_AUTH -> NostrKindStandard.HTTP_AUTH
    KindStandard.INTERESTS -> NostrKindStandard.INTERESTS
    KindStandard.JOB_FEEDBACK -> NostrKindStandard.JOB_FEEDBACK
    KindStandard.LABEL -> NostrKindStandard.LABEL
    KindStandard.LONG_FORM_TEXT_NOTE -> NostrKindStandard.LONG_FORM_TEXT_NOTE
    KindStandard.METADATA -> NostrKindStandard.METADATA
    KindStandard.MUTE_LIST -> NostrKindStandard.MUTE_LIST
    KindStandard.NOSTR_CONNECT -> NostrKindStandard.NOSTR_CONNECT
    KindStandard.OPEN_TIMESTAMPS -> NostrKindStandard.OPEN_TIMESTAMPS
    KindStandard.REACTION -> NostrKindStandard.REACTION
    KindStandard.REPOST -> NostrKindStandard.REPOST
    KindStandard.REPORTING -> NostrKindStandard.REPORTING
    KindStandard.SEAL -> NostrKindStandard.SEAL
    KindStandard.SEARCH_RELAYS -> NostrKindStandard.SEARCH_RELAYS
    KindStandard.TEXT_NOTE -> NostrKindStandard.TEXT_NOTE
    KindStandard.TORRENT_COMMENT -> NostrKindStandard.TORRENT_COMMENT
    KindStandard.TORRENT -> NostrKindStandard.TORRENT
    KindStandard.USER_STATUS -> NostrKindStandard.USER_STATUS
    KindStandard.VIDEOS_CURATION_SET -> NostrKindStandard.VIDEOS_CURATION_SET
    KindStandard.WALLET_CONNECT_INFO -> NostrKindStandard.WALLET_CONNECT_INFO
    KindStandard.WALLET_CONNECT_REQUEST -> NostrKindStandard.WALLET_CONNECT_REQUEST
    KindStandard.WALLET_CONNECT_RESPONSE -> NostrKindStandard.WALLET_CONNECT_RESPONSE
    KindStandard.ZAP_PRIVATE_MESSAGE -> NostrKindStandard.ZAP_PRIVATE_MESSAGE
    KindStandard.ZAP_RECEIPT -> NostrKindStandard.ZAP_RECEIPT
    KindStandard.ZAP_REQUEST -> NostrKindStandard.ZAP_REQUEST
    else -> NostrKindStandard.UNKNOWN
}
fun NostrKindStandard?.toDto(): KindStandard = when(this) {
    NostrKindStandard.TEXT_NOTE -> KindStandard.TEXT_NOTE
    NostrKindStandard.NOSTR_CONNECT -> KindStandard.NOSTR_CONNECT
    NostrKindStandard.REPOST -> KindStandard.REPOST
    NostrKindStandard.COMMENT -> KindStandard.COMMENT
    NostrKindStandard.METADATA -> KindStandard.METADATA
    NostrKindStandard.LONG_FORM_TEXT_NOTE -> KindStandard.LONG_FORM_TEXT_NOTE
    NostrKindStandard.CONTACT_LIST -> KindStandard.CONTACT_LIST
    else -> KindStandard.TEXT_NOTE
}

/// Tag
internal fun Tags.toEntity(): List<String> {
    val tags = mutableListOf<String>()
    for(t in this.toVec()) {
        tags.add(t.toString())
    }
    return tags
}
internal fun List<String>.toTagDto(): Tag {
    return Tag.parse(this)
}

/// Event
internal fun Event.toEntity(authMetaList: List<NostrMetadata>): NostrEvent {
    var authMeta: NostrMetadata? = null
    for(meta in authMetaList) {
        if(meta.npub == author().toBech32()) {
            authMeta = meta
            break
        }
    }
    return toEntity(authMeta)
}
internal fun Event.toEntity(authMeta: NostrMetadata?): NostrEvent {
    val createdAt = LocalDateTime.ofEpochSecond(createdAt().asSecs().toLong(), 0, ZoneOffset.UTC)
    return NostrEvent(
        kind = kind().asStd().toEntity(),
        tags = tags().toEntity(),
        npub = author().toBech32(),
        authMeta = authMeta ?: NostrMetadata.Empty,
        createdAt = createdAt,
        content = content(),
        json = asJson()
    )
}

/// Metadata
internal fun Metadata.toEntity(npub: String) = NostrMetadata(
    npub = npub,
    about = getAbout() ?: "",
    name = getName() ?: "",
    displayName = getDisplayName() ?: "",
    website = getWebsite() ?: "",
    picture = getPicture() ?: "",
    banner = getBanner() ?: "",
    lud06 = getLud06() ?: "",
    lud16 = getLud16() ?: "",
    nip05 = getNip05() ?: ""
)
internal fun Event.toMetadata(npub: String): NostrMetadata {
    return Metadata.fromJson(content()).asRecord().toEntity(npub)
}
fun NostrEvent.toMetadata(npub: String): NostrMetadata {
    return Metadata.fromJson(content).asRecord().toEntity(npub)
}
internal fun MetadataRecord.toEntity(npub: String): NostrMetadata {
    return NostrMetadata(
        npub = npub,
        about = about ?: "",
        name = name ?: "",
        displayName = displayName ?: "",
        website = website ?: "",
        picture = picture ?: "",
        banner = banner ?: "",
        lud06 = lud06 ?: "",
        lud16 = lud16 ?: "",
        nip05 = nip05 ?: "",
    )
}
internal fun NostrMetadata.toDto(): Metadata {
    val metadata = Metadata()
    if(name.isNotBlank()) metadata.setName(name)
    if(displayName.isNotBlank()) metadata.setDisplayName(displayName)
    if(about.isNotBlank()) metadata.setAbout(about)
    if(website.isNotBlank()) metadata.setWebsite(website)
    if(picture.isNotBlank()) metadata.setPicture(picture)
    if(banner.isNotBlank()) metadata.setBanner(banner)
    if(lud16.isNotBlank()) metadata.setLud16(lud16)
    if(lud06.isNotBlank()) metadata.setLud06(lud06)
    if(nip05.isNotBlank()) metadata.setNip05(nip05)
    return metadata
}

/// End