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
package lr.gov.lacra.trace.presentation.settings.notificationsettings

import android.content.Context
import lr.gov.lacra.trace.base.CoreViewBinding
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.base.CoreViewSideEffect
import lr.gov.lacra.trace.domain.settings.models.NotificationSettingsModel

object NotificationSettingsContract {
    data class Binding(
        var notificationsAllowed: Boolean = false,
        var settings: List<NotificationSettingsModel>? = null,
    ) : CoreViewBinding

    sealed class Event : CoreViewEvent {
        data object OnCreate : Event()

        data object NotificationPermissionDenied : Event()
        data class NotificationsAllowedChanged(val context: Context, val enabled: Boolean) : Event()
        data class SettingChanged(val setting: NotificationSettingsModel, val enabled: Boolean) : Event()

        data object OnSaveClicked : Event()
    }

    sealed class Effect : CoreViewSideEffect {
        data class ToggleLoader(val isLoading: Boolean): Effect()
        data class ShowMessage(val message: String): Effect()
        data object RequestNotificationPermission: Effect()
    }
}