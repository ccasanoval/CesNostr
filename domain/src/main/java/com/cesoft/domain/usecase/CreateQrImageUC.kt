package com.cesoft.domain.usecase

import com.cesoft.domain.repo.FilesRepositoryContract
import jakarta.inject.Inject

open class CreateQrImageUC @Inject constructor(
    private val repository: FilesRepositoryContract
) {
    suspend operator fun invoke(value: String, size: Int = 200): Result<String> {
        return repository.createQrImage(value, size)
    }
}
