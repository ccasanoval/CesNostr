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
    KindStandard.TEXT_NOTE -> NostrKindStandard.TEXT_NOTE
    KindStandard.REPOST -> NostrKindStandard.REPOST
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