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
package lr.gov.lacra.trace.presentation.createtransaction.userinfo

import android.content.Context
import androidx.activity.result.ActivityResult
import lr.gov.lacra.trace.base.CoreViewBinding
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.base.CoreViewSideEffect
import lr.gov.lacra.trace.domain.createtransaction.models.CreateTransactionModel
import lr.gov.lacra.trace.domain.createtransaction.models.UserInfoModel
import lr.gov.lacra.trace.utils.PhoneNumberUtils

object UserInfoContract {
    data class Binding(
        var title: String = "User information",
        var nameLabel: String = "Username",

        var username: String = "",
        var email: String = "",
        var phoneRegion: PhoneNumberUtils.PhoneRegion = PhoneNumberUtils.getDefaultPhoneRegion(),
        var phoneNumber: String = "",

        var usernameError: String = "",
        var emailError: String = "",
        var phoneError: String = "",
    ) : CoreViewBinding

    sealed class Event : CoreViewEvent {
        data class OnCreate(val context: Context, val transaction: CreateTransactionModel, val isInvite: Boolean) : Event()
        data class OnUsernameChanged(val username: String) : Event()
        data class OnEmailChanged(val email: String) : Event()
        data class OnPhoneRegionChanged(val phoneRegion: PhoneNumberUtils.PhoneRegion) : Event()
        data class OnPhoneNumberChanged(val phoneNumber: String) : Event()

        data class OnEmailContactsClicked(val context: Context) : Event()
        data class OnPhoneContactsClicked(val context: Context) : Event()
        data class OnContactsPermissionResult(val context: Context, val result: ActivityResult) : Event()

        data object OnConfirm : Event()

        data object OnDownstreamClick : Event()
        data object OnInitialClick : Event()
    }

    sealed class Effect : CoreViewSideEffect {
        data class UserInfoConfirmed(val userInfo: UserInfoModel?) : Effect()

        data class ToggleLoader(val isLoading: Boolean) : Effect()
        data class ShowMessage(val message: String): Effect()
        data object ShowUserExist : Effect()
        data object ShowEmailNotExist : Effect()
        data object ShowPhoneNotExist : Effect()

        data class ConvertToDownstream(val userInfo: UserInfoModel?) : Effect()

        data object OpenEmailContacts : Effect()
        data object OpenPhoneContacts : Effect()
        data object RequestLocationPermission: Effect()
        data object RequestContactsPermission : Effect()

        data object ForceUpdateFields : Effect()
    }
}