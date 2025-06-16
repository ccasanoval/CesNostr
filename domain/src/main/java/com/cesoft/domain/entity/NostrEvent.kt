package com.cesoft.domain.entity

import java.time.LocalDateTime

data class NostrEvent(
    val kind: NostrKindStandard,
    val tags: List<String>,
    val authKey: String,//npub
    val authMeta: NostrMetadata,
    val createdAt: LocalDateTime,
    val content: String,
    val json: String,
)