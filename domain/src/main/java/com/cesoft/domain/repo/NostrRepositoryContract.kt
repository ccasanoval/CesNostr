package com.cesoft.domain.repo

import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrMetadata

interface NostrRepositoryContract {
    suspend fun getUserMetadata(npub: String): Result<NostrMetadata>
    suspend fun getKeys(nsec: String): Result<NostrKeys>
}