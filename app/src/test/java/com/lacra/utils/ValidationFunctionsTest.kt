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
package com.lacra.utils

import org.junit.Assert.*
import org.junit.Test

class ValidationFunctionsTest {

    @Test
    fun `checkIfPasswordFieldsArentEmpty with both fields empty returns false`() {
        // Given
        val passwordFieldEmpty = ""
        val confirmPasswordFieldEmpty = ""

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertFalse(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with password empty returns false`() {
        // Given
        val passwordFieldEmpty = ""
        val confirmPasswordFieldEmpty = "password123"

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertFalse(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with confirm password empty returns false`() {
        // Given
        val passwordFieldEmpty = "password123"
        val confirmPasswordFieldEmpty = ""

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertFalse(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with both fields filled returns true`() {
        // Given
        val passwordFieldEmpty = "password123"
        val confirmPasswordFieldEmpty = "password123"

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertTrue(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with null password returns false`() {
        // Given
        val passwordFieldEmpty: String? = null
        val confirmPasswordFieldEmpty = "password123"

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertFalse(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with null confirm password returns false`() {
        // Given
        val passwordFieldEmpty = "password123"
        val confirmPasswordFieldEmpty: String? = null

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertFalse(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with both fields null returns false`() {
        // Given
        val passwordFieldEmpty: String? = null
        val confirmPasswordFieldEmpty: String? = null

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertFalse(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with default parameters returns false`() {
        // When
        val result = checkIfPasswordFieldsArentEmpty()

        // Then
        assertFalse(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with whitespace only fields returns true`() {
        // Given
        val passwordFieldEmpty = "   "
        val confirmPasswordFieldEmpty = "   "

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertTrue(result)
    }

    @Test
    fun `checkIfPasswordFieldsArentEmpty with mixed whitespace and content returns true`() {
        // Given
        val passwordFieldEmpty = " password123 "
        val confirmPasswordFieldEmpty = " password123 "

        // When
        val result = checkIfPasswordFieldsArentEmpty(passwordFieldEmpty, confirmPasswordFieldEmpty)

        // Then
        assertTrue(result)
    }
}
