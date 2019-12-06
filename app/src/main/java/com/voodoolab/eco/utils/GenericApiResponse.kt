package com.voodoolab.eco.utils

import retrofit2.Response

sealed class GenericApiResponse<T> {

    companion object {

        fun <T> create(error: Throwable): ApiOtherError<T> {
            return ApiOtherError(error.message ?: "unknown error")
        }

        fun <T> create(response: Response<T>): GenericApiResponse<T> {

            if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    return ApiEmptyResponse()
                } else if (response.code() == 401) {
                    return ApiAuthError("401 unauthorized. Token may be invalid")
                } else {
                    return ApiSuccessResponse(body)
                }

            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                return ApiOtherError(errorMsg ?: "unknown error")
            }
        }
    }
}

class ApiEmptyResponse<T>: GenericApiResponse<T>()

data class ApiSuccessResponse<T>(val body: T): GenericApiResponse<T>()

data class ApiOtherError<T>(val errorMessage: String): GenericApiResponse<T>()

data class ApiAuthError<T>(val errorMessage: String): GenericApiResponse<T>()