package com.cesoft.domain.repo

import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.entity.NostrMetadata

interface NostrRepositoryContract {
    suspend fun getKeys(nsec: String): Result<NostrKeys>

    suspend fun createUser(metadata: NostrMetadata): Result<NostrKeys>
    suspend fun getUserMetadata(npub: String): Result<NostrMetadata>

    suspend fun sendEvent(event: NostrEvent): Result<Unit>
    suspend fun fetchEvents(
        kind: NostrKindStandard?,
        authList: List<String>,
        limit: ULong?
    ): Result<List<NostrEvent>>

    suspend fun sendFollowList(followList: List<String>): Result<Unit>
    suspend fun fetchFollowList(): Result<List<NostrMetadata>>

    suspend fun searchAuthors(searchText: String): Result<List<NostrEvent>>
}