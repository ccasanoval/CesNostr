package com.cesoft.domain.usecase

import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.repo.NostrRepositoryContract
import jakarta.inject.Inject

open class FetchFollowListUC @Inject constructor(
    private val repository: NostrRepositoryContract
) {
    suspend operator fun invoke(): Result<List<NostrMetadata>> {
        return repository.fetchFollowList()
    }
}
