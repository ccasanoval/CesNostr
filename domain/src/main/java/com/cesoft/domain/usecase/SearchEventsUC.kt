package com.cesoft.domain.usecase

import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.repo.NostrRepositoryContract
import jakarta.inject.Inject

open class SearchEventsUC @Inject constructor(
    private val repository: NostrRepositoryContract
) {
    suspend operator fun invoke(searchText: String): Result<List<NostrEvent>> {
        return repository.searchAuthors(searchText)
    }
}
