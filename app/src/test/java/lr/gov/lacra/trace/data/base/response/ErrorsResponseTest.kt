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
package lr.gov.lacra.trace.data.base.response

import org.junit.Assert.*
import org.junit.Test

class ErrorsResponseTest {

    @Test
    fun `ErrorsResponse with valid data creates object correctly`() {
        // Given
        val usernameErrors = listOf("Username required", "Username too short")
        val passwordErrors = listOf("Password required", "Password too weak")

        // When
        val result = ErrorsResponse(usernameErrors, passwordErrors)

        // Then
        assertEquals(usernameErrors, result.username)
        assertEquals(passwordErrors, result.password)
    }

    @Test
    fun `ErrorsResponse with null values creates object correctly`() {
        // When
        val result = ErrorsResponse(null, null)

        // Then
        assertNull(result.username)
        assertNull(result.password)
    }

    @Test
    fun `ErrorsResponse with empty lists creates object correctly`() {
        // Given
        val emptyList = emptyList<String>()

        // When
        val result = ErrorsResponse(emptyList, emptyList)

        // Then
        assertEquals(emptyList, result.username)
        assertEquals(emptyList, result.password)
    }

    @Test
    fun `ErrorsResponse with mixed null and valid values creates object correctly`() {
        // Given
        val usernameErrors = listOf("Username required")
        val passwordErrors: List<String>? = null

        // When
        val result = ErrorsResponse(usernameErrors, passwordErrors)

        // Then
        assertEquals(usernameErrors, result.username)
        assertNull(result.password)
    }

    @Test
    fun `ErrorsResponse with single error creates object correctly`() {
        // Given
        val usernameErrors = listOf("Username required")
        val passwordErrors = listOf("Password required")

        // When
        val result = ErrorsResponse(usernameErrors, passwordErrors)

        // Then
        assertEquals(1, result.username?.size)
        assertEquals(1, result.password?.size)
        assertEquals("Username required", result.username?.first())
        assertEquals("Password required", result.password?.first())
    }

    @Test
    fun `ErrorsResponse with multiple errors creates object correctly`() {
        // Given
        val usernameErrors = listOf("Error 1", "Error 2", "Error 3")
        val passwordErrors = listOf("Password Error 1", "Password Error 2")

        // When
        val result = ErrorsResponse(usernameErrors, passwordErrors)

        // Then
        assertEquals(3, result.username?.size)
        assertEquals(2, result.password?.size)
        assertTrue(result.username?.contains("Error 1") == true)
        assertTrue(result.password?.contains("Password Error 1") == true)
    }
}
