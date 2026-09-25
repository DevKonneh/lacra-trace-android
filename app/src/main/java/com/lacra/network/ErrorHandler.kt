/*
 * Copyright (c) 2025 EFI (https://efi.int/)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.lacra.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.lacra.R
import com.lacra.data.base.common.BaseResult
import com.lacra.data.base.common.wrapResult
import com.lacra.data.base.response.BaseResponse
import com.lacra.network.error.NetworkError
import com.lacra.network.error.ParsedError
import com.lacra.network.error.ServerError
import com.lacra.providers.ResourceProvider
import retrofit2.Response
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorHandler(
    private val resourceProvider: ResourceProvider
) {
    fun parseError(throwable: Throwable): String {
        throwable.printStackTrace()
        return when (throwable) {
            is ServerError -> throwable.message
            is NetworkError -> {
                when (throwable.origin) {
                    is ConnectException,
                    is SocketTimeoutException,
                    is UnknownHostException,
                    is SocketException -> resourceProvider.getString(R.string.connection_error)

                    is InterruptedIOException -> resourceProvider.getString(R.string.network_error)
                    else -> resourceProvider.getString(R.string.unexpected_network_error)
                }
            }

            else -> resourceProvider.getString(R.string.unexpected_server_error)
        }
    }
}

suspend fun <T : Any> handleResponse(callback: suspend () -> Response<T>): BaseResult<T> {
    return try {
        val response = callback()
        if (response.isSuccessful) {
            response.wrapResult { response.body() }
        } else {
            val errorResponse = response.convertErrorBody()
            val errorBody = response.errorBody()?.string() ?: "No error body"
            val errorMessage = "HTTP ${response.code()}: ${errorResponse?.message ?: response.message()}"
            Log.e("NetworkError", errorMessage)
            Log.e("NetworkError", "Error body: $errorBody")
            BaseResult.Error(ServerError(response.code(), errorResponse))
        }
    } catch (e: Exception) {
        Log.e("NetworkError", "Exception during network call", e)
        BaseResult.Error(NetworkError(origin = e))
    }
}

inline fun <T : Any, R : Any> BaseResult<T>.mapResult(mapper: (T?) -> R?): BaseResult<R> {
    if (this is BaseResult.Success) return BaseResult.Success(mapper(data))
    return this as BaseResult.Error
}

fun JsonObject.parse(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    keySet().forEach {
        map[it] = get(it).asString
    }
    return map
}

fun <T> Response<T>.convertErrorBody(): BaseResponse? {
    return try {
        Gson().fromJson(errorBody()?.charStream(), BaseResponse::class.java)
    } catch (e: Exception) {
        null
    }
}
