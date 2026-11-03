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
package lr.gov.lacra.trace.utils

import android.util.Patterns

object ValidationUtils {
    private val phoneNumberRegex = Regex("^\\+[1-9]\\d{7,14}\$")
    private val passwordRegex = Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$")

    enum class ValidationState { Empty, Invalid, Valid }

    fun validateEmail(email: String): ValidationState {
        return if (email.isEmpty()) {
            ValidationState.Empty
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ValidationState.Invalid
        } else {
            ValidationState.Valid
        }
    }

    fun validatePhoneNumber(phoneCode: Int, phoneNumber: String): ValidationState {
        val phone = "+${phoneCode}$phoneNumber"

        return if (phoneNumber.isEmpty()) {
            ValidationState.Empty
        } else if (!phone.matches(phoneNumberRegex)) {
            ValidationState.Invalid
        } else {
            ValidationState.Valid
        }
    }

    fun validatePassword(password: String): ValidationState {
        return if (password.isEmpty()) {
            ValidationState.Empty
        } else if (!password.matches(passwordRegex)) {
            ValidationState.Invalid
        } else {
            ValidationState.Valid
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): ValidationState {
        return if (confirmPassword.isEmpty()) {
            ValidationState.Empty
        } else if (confirmPassword != password) {
            ValidationState.Invalid
        } else {
            ValidationState.Valid
        }
    }
}