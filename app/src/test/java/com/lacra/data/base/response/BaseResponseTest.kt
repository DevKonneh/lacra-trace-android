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

import com.lacra.domain.auth.models.BaseModel
import org.junit.Assert.*
import org.junit.Test

class BaseResponseTest {

    @Test
    fun `toDomain converts BaseResponse to BaseModel correctly`() {
        // Given
        val baseResponse = BaseResponse(
            success = true,
            message = "Success message",
            code = "200",
            errors = ErrorsResponse(
                username = listOf("Username error"),
                password = listOf("Password error")
            )
        )

        // When
        val result = baseResponse.toDomain()

        // Then
        assertTrue(result.success)
        assertEquals("Success message", result.message)
        assertEquals("200", result.code)
        assertEquals(listOf("Username error"), result.usernameError)
        assertEquals(listOf("Password error"), result.passwordError)
    }

    @Test
    fun `toDomain handles null values correctly`() {
        // Given
        val baseResponse = BaseResponse(
            success = null,
            message = null,
            code = null,
            errors = null
        )

        // When
        val result = baseResponse.toDomain()

        // Then
        assertFalse(result.success)
        assertEquals("", result.message)
        assertEquals("", result.code)
        assertTrue(result.usernameError.isEmpty())
        assertTrue(result.passwordError.isEmpty())
    }

    @Test
    fun `toDomain handles partial null values correctly`() {
        // Given
        val baseResponse = BaseResponse(
            success = true,
            message = "Partial message",
            code = null,
            errors = ErrorsResponse(
                username = null,
                password = listOf("Password error")
            )
        )

        // When
        val result = baseResponse.toDomain()

        // Then
        assertTrue(result.success)
        assertEquals("Partial message", result.message)
        assertEquals("", result.code)
        assertTrue(result.usernameError.isEmpty())
        assertEquals(listOf("Password error"), result.passwordError)
    }

    @Test
    fun `toDomain handles empty error lists correctly`() {
        // Given
        val baseResponse = BaseResponse(
            success = true,
            message = "Success",
            code = "200",
            errors = ErrorsResponse(
                username = emptyList(),
                password = emptyList()
            )
        )

        // When
        val result = baseResponse.toDomain()

        // Then
        assertTrue(result.success)
        assertEquals("Success", result.message)
        assertEquals("200", result.code)
        assertTrue(result.usernameError.isEmpty())
        assertTrue(result.passwordError.isEmpty())
    }

    @Test
    fun `toDomain handles multiple errors correctly`() {
        // Given
        val baseResponse = BaseResponse(
            success = false,
            message = "Validation failed",
            code = "400",
            errors = ErrorsResponse(
                username = listOf("Username required", "Username too short"),
                password = listOf("Password required", "Password too weak")
            )
        )

        // When
        val result = baseResponse.toDomain()

        // Then
        assertFalse(result.success)
        assertEquals("Validation failed", result.message)
        assertEquals("400", result.code)
        assertEquals(listOf("Username required", "Username too short"), result.usernameError)
        assertEquals(listOf("Password required", "Password too weak"), result.passwordError)
    }
}
