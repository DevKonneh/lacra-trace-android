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
package com.lacra.extensions

import com.lacra.exceptions.MappingException
import org.junit.Assert.*
import org.junit.Test

class MappingExtTest {

    @Test
    fun `getOrThrowDefaultError with non-null value returns the value`() {
        // Given
        val value = "test"
        val name = "testField"

        // When
        val result: String = value.getOrThrowDefaultError(name)

        // Then
        assertEquals("test", result)
    }

    @Test
    fun `getOrThrowDefaultError with null value throws MappingException`() {
        // Given
        val value: String? = null
        val name = "testField"

        // When & Then
        try {
            val result: String = value.getOrThrowDefaultError(name)
            fail("Expected MappingException to be thrown")
        } catch (e: MappingException) {
            assertEquals("testField can't be null", e.message)
        }
    }

    @Test
    fun `getOrThrowDefaultError with null value and custom name throws correct exception`() {
        // Given
        val value: Int? = null
        val name = "userId"

        // When & Then
        try {
            val result: Int = value.getOrThrowDefaultError(name)
            fail("Expected MappingException to be thrown")
        } catch (e: MappingException) {
            assertEquals("userId can't be null", e.message)
        }
    }

    @Test
    fun `getOrThrowDefaultError with non-null integer returns the value`() {
        // Given
        val value = 123
        val name = "userId"

        // When
        val result: Int = value.getOrThrowDefaultError(name)

        // Then
        assertEquals(123, result)
    }

    @Test
    fun `getOrThrowDefaultError with non-null boolean returns the value`() {
        // Given
        val value = true
        val name = "isActive"

        // When
        val result: Boolean = value.getOrThrowDefaultError(name)

        // Then
        assertTrue(result)
    }

    @Test
    fun `getOrThrowDefaultError with non-null double returns the value`() {
        // Given
        val value = 3.14
        val name = "pi"

        // When
        val result: Double = value.getOrThrowDefaultError(name)

        // Then
        assertEquals(3.14, result, 0.001)
    }

    @Test
    fun `getOrThrowDefaultError with non-null list returns the value`() {
        // Given
        val value = listOf("a", "b", "c")
        val name = "items"

        // When
        val result: List<String> = value.getOrThrowDefaultError(name)

        // Then
        assertEquals(listOf("a", "b", "c"), result)
    }

    @Test
    fun `getOrThrowDefaultError with empty string returns the value`() {
        // Given
        val value = ""
        val name = "emptyField"

        // When
        val result: String = value.getOrThrowDefaultError(name)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `getOrThrowDefaultError with zero returns the value`() {
        // Given
        val value = 0
        val name = "zeroValue"

        // When
        val result: Int = value.getOrThrowDefaultError(name)

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `getOrThrowDefaultError with false returns the value`() {
        // Given
        val value = false
        val name = "falseValue"

        // When
        val result: Boolean = value.getOrThrowDefaultError(name)

        // Then
        assertFalse(result)
    }
}
