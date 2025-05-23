package com.cesoft.domain.repo

interface PrefsRepositoryContract {
    suspend fun readPrivateKey(): String?
    suspend fun writePrivateKey(value: String?)
}