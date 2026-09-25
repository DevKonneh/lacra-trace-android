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
package com.lacra.data.base.common

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class BaseResult<out T : Any> {
    data class Success<out T : Any>(val data: T?) : BaseResult<T>()
    data class Error(val exception: Throwable) : BaseResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}

inline fun <T, R : Any> T.wrapResult(block: T.() -> R?): BaseResult<R> {
    return runCatching(block).fold(
        onSuccess = { BaseResult.Success(it) },
        onFailure = { BaseResult.Error(it) }
    )
}

inline fun BaseResult<Unit>.onSuccess(block: () -> Unit): BaseResult<Unit> {
    if (this is BaseResult.Success) block()
    return this
}

inline fun <T : Any> BaseResult<T>.onSuccess(block: (T?) -> Unit): BaseResult<T> {
    if (this is BaseResult.Success) block(data)
    return this
}

inline fun <T : Any> BaseResult<T>.onError(block: (Throwable) -> Unit): BaseResult<T> {
    if (this is BaseResult.Error) block(exception)
    return this
}

inline fun <T : Any, R : Any> BaseResult<T>.map(transform: (T?) -> R?): BaseResult<R> {
    return when (this) {
        is BaseResult.Success -> {
            BaseResult.Success(transform(data))
        }

        is BaseResult.Error -> {
            this
        }
    }
}
