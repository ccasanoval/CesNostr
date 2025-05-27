package com.cesoft.domain.usecase

import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.repo.NostrRepositoryContract
import jakarta.inject.Inject

open class CreateUserUC @Inject constructor(
    private val repository: NostrRepositoryContract
) {
    suspend operator fun invoke(metadata: NostrMetadata): Result<NostrKeys> {
        return repository.createUser(metadata)
    }
}
