package com.cesoft.domain.usecase

import com.cesoft.domain.repo.PrefsRepositoryContract
import jakarta.inject.Inject

open class SavePrivateKeyUC @Inject constructor(
    private val repository: PrefsRepositoryContract
) {
    suspend operator fun invoke(nsec: String?) {
        return repository.writePrivateKey(nsec)
    }
}
