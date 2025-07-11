package com.cesoft.domain

sealed class AppError: Throwable() {

    data object NotKnownError: AppError() {
        private fun readResolve(): Any = NotKnownError
    }

    data class InternalError(val code: Int = 0, val msg: String = ""): AppError()
    data class NetworkException(val code: Int = 0, val msg: String = ""): AppError()
    data class NostrError(val failed: Map<String, String>): AppError()

    data object NotFound: AppError() {
        private fun readResolve(): Any = NotFound
    }

    data object InvalidNostrKey: AppError() {
        private fun readResolve(): Any = InvalidNostrKey
    }
    data object InvalidMetadata: AppError() {
        private fun readResolve(): Any = InvalidMetadata
    }
}
