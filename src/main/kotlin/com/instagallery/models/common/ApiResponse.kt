package com.instagallery.models.common

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val status: String,
    val data: T? = null,
    val error: ErrorDetails? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> = ApiResponse("SUCCESS", data = data, message = message)
        fun error(code: String, message: String, details: List<String>? = null): ApiResponse<Nothing> =
            ApiResponse("ERROR", error = ErrorDetails(code, message, details))
    }
}

@Serializable
data class ErrorDetails(
    val code: String,
    val message: String,
    val details: List<String>? = null
)
