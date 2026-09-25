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
package com.lacra.presentation.auth.createNewPassword

import com.lacra.base.CoreViewBinding
import com.lacra.base.CoreViewEvent
import com.lacra.base.CoreViewSideEffect

object CreateNewPasswordContract {
    data class Binding(
        var password: String = "",
        var confirmPassword: String = "",
        var passwordError: String = "",
        var confirmPasswordError: String = "",
        var confirmEnabled: Boolean = false,
    ) : CoreViewBinding

    sealed class Event : CoreViewEvent {
        data class OnCreate(val username: String?, val code: String?) : Event()

        data class OnPasswordChanged(val password: String) : Event()
        data class OnConfirmPasswordChanged(val confirmPassword: String) : Event()

        data object OnConfirmClick : Event()
    }

    sealed class Effect : CoreViewSideEffect {
        data class ToggleLoader(val isLoading: Boolean) : Effect()
        data class ShowMessage(val message: String): Effect()

        data object NavigateLogin : Effect()
    }
}