package com.cesoft.domain.usecase

import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.repo.NostrRepositoryContract
import jakarta.inject.Inject

open class GetKeysUC @Inject constructor(
    private val repository: NostrRepositoryContract
) {
    suspend operator fun invoke(nsec: String): Result<NostrKeys> {
        return repository.getKeys(nsec)
    }
}
