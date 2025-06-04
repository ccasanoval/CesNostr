package com.cesoft.domain.repo

interface FilesRepositoryContract {
    suspend fun createQrImage(value: String, size: Int): Result<String>
}