package com.cesoft.domain.usecase

import com.cesoft.domain.repo.NostrRepositoryContract
import jakarta.inject.Inject

open class SendFollowListUC @Inject constructor(
    private val repository: NostrRepositoryContract
) {
    suspend operator fun invoke(
        authList: List<String>,
    ): Result<Unit> {
        return repository.sendFollowList(authList)
    }
}
