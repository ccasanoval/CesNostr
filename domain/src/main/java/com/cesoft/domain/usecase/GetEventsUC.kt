package com.cesoft.domain.usecase

import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.repo.NostrRepositoryContract
import jakarta.inject.Inject

open class GetEventsUC @Inject constructor(
    private val repository: NostrRepositoryContract
) {
    suspend operator fun invoke(
        kind: NostrKindStandard? = null,
        authList: List<String>,
    ): Result<List<NostrEvent>> {
        return repository.getEvents(kind, authList)
    }
}
