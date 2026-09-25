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

import org.junit.Assert.*
import org.junit.Test
import java.lang.RuntimeException

class BaseResultTest {

    @Test
    fun `Success toString returns correct format`() {
        // Given
        val data = "test data"
        val result = BaseResult.Success(data)

        // When
        val string = result.toString()

        // Then
        assertTrue(string.contains("Success"))
        assertTrue(string.contains("data=$data"))
    }

    @Test
    fun `Error toString returns correct format`() {
        // Given
        val exception = RuntimeException("Test error")
        val result = BaseResult.Error(exception)

        // When
        val string = result.toString()

        // Then
        assertTrue(string.contains("Error"))
        assertTrue(string.contains("exception=$exception"))
    }

    @Test
    fun `wrapResult with successful operation returns Success`() {
        // Given
        val input = "test"
        val expectedOutput = "TEST"

        // When
        val result = input.wrapResult { uppercase() }

        // Then
        assertTrue(result is BaseResult.Success)
        assertEquals(expectedOutput, (result as BaseResult.Success).data)
    }

    @Test
    fun `wrapResult with null result returns Success with null`() {
        // Given
        val input: String? = null

        // When
        val result = input.wrapResult { this }

        // Then
        assertTrue(result is BaseResult.Success)
        assertNull((result as BaseResult.Success).data)
    }

    @Test
    fun `wrapResult with exception returns Error`() {
        // Given
        val input = "test"
        val expectedException = RuntimeException("Test error")

        // When
        val result = input.wrapResult { throw expectedException }

        // Then
        assertTrue(result is BaseResult.Error)
        assertEquals(expectedException, (result as BaseResult.Error).exception)
    }

    @Test
    fun `onSuccess with Unit Success executes block`() {
        // Given
        val result = BaseResult.Success(Unit)
        var executed = false

        // When
        val returnedResult = result.onSuccess { executed = true }

        // Then
        assertTrue(executed)
        assertEquals(result, returnedResult)
    }

    @Test
    fun `onSuccess with Unit Error does not execute block`() {
        // Given
        val result = BaseResult.Error(RuntimeException("Test"))
        var executed = false

        // When
        val returnedResult = result.onSuccess { executed = true }

        // Then
        assertFalse(executed)
        assertEquals(result, returnedResult)
    }

    @Test
    fun `onSuccess with data Success executes block with data`() {
        // Given
        val data = "test data"
        val result = BaseResult.Success(data)
        var receivedData: String? = null

        // When
        val returnedResult = result.onSuccess { receivedData = it }

        // Then
        assertEquals(data, receivedData)
        assertEquals(result, returnedResult)
    }

    @Test
    fun `onSuccess with data Error does not execute block`() {
        // Given
        val result = BaseResult.Error(RuntimeException("Test"))
        var executed = false

        // When
        val returnedResult = result.onSuccess { executed = true }

        // Then
        assertFalse(executed)
        assertEquals(result, returnedResult)
    }

    @Test
    fun `onError with Error executes block with exception`() {
        // Given
        val exception = RuntimeException("Test error")
        val result = BaseResult.Error(exception)
        var receivedException: Throwable? = null

        // When
        val returnedResult = result.onError { receivedException = it }

        // Then
        assertEquals(exception, receivedException)
        assertEquals(result, returnedResult)
    }

    @Test
    fun `onError with Success does not execute block`() {
        // Given
        val result = BaseResult.Success("test")
        var executed = false

        // When
        val returnedResult = result.onError { executed = true }

        // Then
        assertFalse(executed)
        assertEquals(result, returnedResult)
    }

    @Test
    fun `map with Success transforms data`() {
        // Given
        val data = "test"
        val result = BaseResult.Success(data)

        // When
        val mappedResult = result.map<String, String> { it?.uppercase() }

        // Then
        assertTrue(mappedResult is BaseResult.Success)
        assertEquals("TEST", (mappedResult as BaseResult.Success).data)
    }

    @Test
    fun `map with Success and null transform returns Success with null`() {
        // Given
        val data = "test"
        val result = BaseResult.Success(data)

        // When
        val mappedResult = result.map { null }

        // Then
        assertTrue(mappedResult is BaseResult.Success)
        assertNull((mappedResult as BaseResult.Success).data)
    }

    @Test
    fun `map with Error returns same Error`() {
        // Given
        val exception = RuntimeException("Test error")
        val result = BaseResult.Error(exception)

        // When
        val mappedResult = result.map<String, String> { it?.uppercase() }

        // Then
        assertTrue(mappedResult is BaseResult.Error)
        assertEquals(exception, (mappedResult as BaseResult.Error).exception)
    }

    @Test
    fun `map with Success and transform exception returns Error`() {
        // Given
        val data = "test"
        val result = BaseResult.Success(data)
        val transformException = RuntimeException("Transform error")

        // When & Then
        try {
            val mappedResult = result.map<String, String> { throw transformException }
            fail("Expected RuntimeException to be thrown")
        } catch (e: RuntimeException) {
            assertEquals(transformException.message, e.message)
        }
    }
}
