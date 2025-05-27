package com.cesoft.domain.usecase

import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.repo.NostrRepositoryContract
import jakarta.inject.Inject


open class GetUserMetadataUC @Inject constructor(
    private val repository: NostrRepositoryContract
) {
    suspend operator fun invoke(npub: String): Result<NostrMetadata> {
        return repository.getUserMetadata(npub)
    }
}
