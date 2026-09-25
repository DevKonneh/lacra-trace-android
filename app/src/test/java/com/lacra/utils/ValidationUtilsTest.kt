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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ValidationUtilsTest {

    @Test
    fun `validateEmail with valid email returns Valid`() {
        // Given
        val email = "test@example.com"

        // When
        val result = ValidationUtils.validateEmail(email)

        // Then
        assertEquals(ValidationUtils.ValidationState.Valid, result)
    }

    @Test
    fun `validateEmail with invalid email returns Invalid`() {
        // Given
        val email = "invalid-email"

        // When
        val result = ValidationUtils.validateEmail(email)

        // Then
        assertEquals(ValidationUtils.ValidationState.Invalid, result)
    }

    @Test
    fun `validateEmail with empty email returns Empty`() {
        // Given
        val email = ""

        // When
        val result = ValidationUtils.validateEmail(email)

        // Then
        assertEquals(ValidationUtils.ValidationState.Empty, result)
    }

    @Test
    fun `validatePhoneNumber with valid phone returns Valid`() {
        // Given
        val phoneCode = 1
        val phoneNumber = "2345678901"

        // When
        val result = ValidationUtils.validatePhoneNumber(phoneCode, phoneNumber)

        // Then
        assertEquals(ValidationUtils.ValidationState.Valid, result)
    }

    @Test
    fun `validatePhoneNumber with invalid phone returns Invalid`() {
        // Given
        val phoneCode = 1
        val phoneNumber = "123"

        // When
        val result = ValidationUtils.validatePhoneNumber(phoneCode, phoneNumber)

        // Then
        assertEquals(ValidationUtils.ValidationState.Invalid, result)
    }

    @Test
    fun `validatePhoneNumber with empty phone returns Empty`() {
        // Given
        val phoneCode = 1
        val phoneNumber = ""

        // When
        val result = ValidationUtils.validatePhoneNumber(phoneCode, phoneNumber)

        // Then
        assertEquals(ValidationUtils.ValidationState.Empty, result)
    }

    @Test
    fun `validatePassword with valid password returns Valid`() {
        // Given
        val password = "Password123"

        // When
        val result = ValidationUtils.validatePassword(password)

        // Then
        assertEquals(ValidationUtils.ValidationState.Valid, result)
    }

    @Test
    fun `validatePassword with invalid password returns Invalid`() {
        // Given
        val password = "password"

        // When
        val result = ValidationUtils.validatePassword(password)

        // Then
        assertEquals(ValidationUtils.ValidationState.Invalid, result)
    }

    @Test
    fun `validatePassword with empty password returns Empty`() {
        // Given
        val password = ""

        // When
        val result = ValidationUtils.validatePassword(password)

        // Then
        assertEquals(ValidationUtils.ValidationState.Empty, result)
    }

    @Test
    fun `validateConfirmPassword with matching passwords returns Valid`() {
        // Given
        val password = "Password123"
        val confirmPassword = "Password123"

        // When
        val result = ValidationUtils.validateConfirmPassword(password, confirmPassword)

        // Then
        assertEquals(ValidationUtils.ValidationState.Valid, result)
    }

    @Test
    fun `validateConfirmPassword with non-matching passwords returns Invalid`() {
        // Given
        val password = "Password123"
        val confirmPassword = "Password456"

        // When
        val result = ValidationUtils.validateConfirmPassword(password, confirmPassword)

        // Then
        assertEquals(ValidationUtils.ValidationState.Invalid, result)
    }

    @Test
    fun `validateConfirmPassword with empty confirm password returns Empty`() {
        // Given
        val password = "Password123"
        val confirmPassword = ""

        // When
        val result = ValidationUtils.validateConfirmPassword(password, confirmPassword)

        // Then
        assertEquals(ValidationUtils.ValidationState.Empty, result)
    }

    @Test
    fun `validatePassword with password too short returns Invalid`() {
        // Given
        val password = "Pass1"

        // When
        val result = ValidationUtils.validatePassword(password)

        // Then
        assertEquals(ValidationUtils.ValidationState.Invalid, result)
    }

    @Test
    fun `validatePassword with password without uppercase returns Invalid`() {
        // Given
        val password = "password123"

        // When
        val result = ValidationUtils.validatePassword(password)

        // Then
        assertEquals(ValidationUtils.ValidationState.Invalid, result)
    }

    @Test
    fun `validatePassword with password without digit returns Invalid`() {
        // Given
        val password = "Password"

        // When
        val result = ValidationUtils.validatePassword(password)

        // Then
        assertEquals(ValidationUtils.ValidationState.Invalid, result)
    }
}
