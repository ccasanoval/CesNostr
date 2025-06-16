package com.cesoft.domain.repo

import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.entity.NostrMetadata

interface NostrRepositoryContract {
    suspend fun createUser(metadata: NostrMetadata): Result<NostrKeys>
    suspend fun getUserMetadata(npub: String): Result<NostrMetadata>
    suspend fun getKeys(nsec: String): Result<NostrKeys>
    suspend fun getEvents(kind: NostrKindStandard?, authList: List<String>): Result<List<NostrEvent>>
}