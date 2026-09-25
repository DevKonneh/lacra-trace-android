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
package com.lacra.data.base.response

import org.junit.Assert.*
import org.junit.Test

class TokensResponseTest {

    @Test
    fun `TokensResponse with valid tokens creates object correctly`() {
        // Given
        val accessToken = "access_token_123"
        val refreshToken = "refresh_token_456"

        // When
        val result = TokensResponse(accessToken, refreshToken)

        // Then
        assertEquals(accessToken, result.access)
        assertEquals(refreshToken, result.refresh)
    }

    @Test
    fun `TokensResponse with null values creates object correctly`() {
        // When
        val result = TokensResponse(null, null)

        // Then
        assertNull(result.access)
        assertNull(result.refresh)
    }

    @Test
    fun `TokensResponse with empty strings creates object correctly`() {
        // Given
        val accessToken = ""
        val refreshToken = ""

        // When
        val result = TokensResponse(accessToken, refreshToken)

        // Then
        assertEquals("", result.access)
        assertEquals("", result.refresh)
    }

    @Test
    fun `TokensResponse with mixed null and valid values creates object correctly`() {
        // Given
        val accessToken = "access_token_123"
        val refreshToken: String? = null

        // When
        val result = TokensResponse(accessToken, refreshToken)

        // Then
        assertEquals(accessToken, result.access)
        assertNull(result.refresh)
    }

    @Test
    fun `TokensResponse with long tokens creates object correctly`() {
        // Given
        val accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        val refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

        // When
        val result = TokensResponse(accessToken, refreshToken)

        // Then
        assertEquals(accessToken, result.access)
        assertEquals(refreshToken, result.refresh)
        assertTrue(result.access!!.length > 100)
        assertTrue(result.refresh!!.length > 100)
    }

    @Test
    fun `TokensResponse with special characters creates object correctly`() {
        // Given
        val accessToken = "access_token_with_special_chars_!@#$%^&*()"
        val refreshToken = "refresh_token_with_special_chars_!@#$%^&*()"

        // When
        val result = TokensResponse(accessToken, refreshToken)

        // Then
        assertEquals(accessToken, result.access)
        assertEquals(refreshToken, result.refresh)
        assertTrue(result.access!!.contains("!"))
        assertTrue(result.refresh!!.contains("@"))
    }
}
