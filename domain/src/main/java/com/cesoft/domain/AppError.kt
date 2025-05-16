package com.cesoft.domain

sealed class AppError: Throwable() {

    data object UnknownError: AppError() {
        private fun readResolve(): Any = UnknownError
    }

    data class InternalError(val code: Int = 0, val msg: String = ""): AppError()
    data class NetworkException(val code: Int = 0, val msg: String = ""): AppError()

    data object NotFound: AppError() {
        private fun readResolve(): Any = NotFound
    }

    companion object {}
}