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
package com.lacra.presentation.auth.forgotPassword

import android.content.Context
import com.lacra.base.CoreViewBinding
import com.lacra.base.CoreViewEvent
import com.lacra.base.CoreViewSideEffect
import com.lacra.utils.PhoneNumberUtils

enum class ForgotPasswordScreenState { Email, Phone }

object ForgotPasswordContract {
    data class Binding(
        var state: ForgotPasswordScreenState = ForgotPasswordScreenState.Email,
        var description: String = "",

        var email: String = "",
        var phoneRegion: PhoneNumberUtils.PhoneRegion = PhoneNumberUtils.getDefaultPhoneRegion(),
        var phoneNumber: String = "",
        var password: String = "",

        var emailError: String = "",
        var phoneError: String = "",
        var passwordError: String = "",

        var sendCodeEnabled: Boolean = false,
        var changeStatusButtonTitle: String = "",
    ) : CoreViewBinding

    sealed class Event : CoreViewEvent {
        data class OnCreate(val context: Context, val email: String?, val phone: String?) : Event()

        data class OnEmailChanged(val email: String) : Event()
        data class OnPhoneRegionChanged(val phoneRegion: PhoneNumberUtils.PhoneRegion) : Event()
        data class OnPhoneChanged(val phone: String) : Event()

        data object OnSendCodeClick : Event()
        data object OnChangeStatusClick : Event()
    }

    sealed class Effect : CoreViewSideEffect {
        data object RequestLocationPermission: Effect()
        data class ToggleLoader(val isLoading: Boolean) : Effect()
        data class ShowMessage(val message: String): Effect()

        data class NavigateToEmailOtp(val email: String) : Effect()
        data class NavigateToPhoneOtp(val phone: String) : Effect()
    }
}