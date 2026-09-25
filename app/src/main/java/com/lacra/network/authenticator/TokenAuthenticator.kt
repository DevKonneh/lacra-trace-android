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
package com.lacra.network.authenticator

import com.lacra.extensions.toBearerToken
import com.lacra.providers.SharedPreferencesProvider
import com.lacra.utils.AUTHORIZATION_HEADER
import com.lacra.utils.BEARER_HEADER
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = sharedPreferencesProvider.getAuthToken()
        val refreshToken = sharedPreferencesProvider.getRefreshToken()

        if (!isRequestWithAccessToken(response) || accessToken == null || refreshToken == null) {
            return null
        }

        synchronized(this) {
            val newAccessToken = runBlocking {
                sessionManager.refreshAccessToken(refreshToken)
            }
            if (newAccessToken != null) {
                return newRequestWithAccessToken(response.request, newAccessToken)
            }
        }

        return null
    }

    private fun isRequestWithAccessToken(response: Response): Boolean {
        val header = response.request.header(AUTHORIZATION_HEADER)
        return header != null && header.startsWith(BEARER_HEADER)
    }

    private fun newRequestWithAccessToken(
        request: Request,
        accessToken: String,
    ): Request {
        return request.newBuilder()
            .header(AUTHORIZATION_HEADER, accessToken.toBearerToken())
            .build()
    }
}
