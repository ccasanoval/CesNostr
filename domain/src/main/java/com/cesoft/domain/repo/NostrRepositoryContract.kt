package com.cesoft.domain.repo

import com.cesoft.domain.entity.NostrMetadata

interface NostrRepositoryContract {
    suspend fun getUserMetadata(npub: String): Result<NostrMetadata>
}