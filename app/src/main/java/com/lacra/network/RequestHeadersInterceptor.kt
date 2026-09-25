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

import android.content.Context
import com.lacra.extensions.toBearerToken
import com.lacra.providers.SharedPreferencesProvider
import com.lacra.utils.AUTHORIZATION_HEADER
import com.lacra.utils.AppLocaleManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class RequestHeadersInterceptor(
    private val preferencesProvider: SharedPreferencesProvider,
    private val appLocaleManager: AppLocaleManager,
    private val context: Context,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("Accept", "application/json")
        builder.addHeader("Accept-language", appLocaleManager.getLanguageCode(context))
        addAuthToken(builder)
        return chain.proceed(builder.build())
    }

    private fun addAuthToken(builder: Request.Builder): Request.Builder {
        val token = preferencesProvider.getAuthToken()
        if (token != null) {
            builder.addHeader(AUTHORIZATION_HEADER, token.toBearerToken())
        }
        return builder
    }
}
