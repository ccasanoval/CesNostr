package com.cesoft.domain.usecase

import com.cesoft.domain.repo.PrefsRepositoryContract
import jakarta.inject.Inject

open class ReadPrivateKeyUC @Inject constructor(
    private val repository: PrefsRepositoryContract
) {
    suspend operator fun invoke(): String? {
        return repository.readPrivateKey()
    }
}
