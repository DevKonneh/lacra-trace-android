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

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.data.base.common.onError
import lr.gov.lacra.trace.data.base.common.onSuccess
import lr.gov.lacra.trace.domain.settings.SettingsInteractor
import lr.gov.lacra.trace.domain.settings.models.NotificationSettingsModel
import lr.gov.lacra.trace.network.ErrorHandler
import lr.gov.lacra.trace.providers.ResourceProvider
import lr.gov.lacra.trace.providers.SharedPreferencesProvider
import lr.gov.lacra.trace.services.MyFirebaseMessagingService.Companion.PUSH_NOTIFICATIONS_CHANNEL_ID

class NotificationSettingsViewModel(
    private val interactor: SettingsInteractor,
    private val errorHandler: ErrorHandler,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<NotificationSettingsContract.Binding>() {

    private var notificationsAllowed: Boolean = true
    private var settings: List<NotificationSettingsModel>? = null

    override fun createBinding(): NotificationSettingsContract.Binding {
        return NotificationSettingsContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is NotificationSettingsContract.Event.OnCreate -> onCreate()
            is NotificationSettingsContract.Event.NotificationPermissionDenied -> notificationPermissionDenied()
            is NotificationSettingsContract.Event.NotificationsAllowedChanged -> notificationsAllowedChanged(event.context, event.enabled)
            is NotificationSettingsContract.Event.SettingChanged -> settingChanged(event.setting, event.enabled)
            is NotificationSettingsContract.Event.OnSaveClicked -> onSaveClick()
        }
    }

    override fun copyBinding(binding: NotificationSettingsContract.Binding): NotificationSettingsContract.Binding {
        return binding.copy()
    }

    private fun onCreate() {
        notificationsAllowed = sharedPreferencesProvider.isNotificationsAllowed()

        if (settings.isNullOrEmpty()) {
            launch {
                settings = interactor.getNotificationSettingsFromDB()
                updateView()

                loadNotificationSettings()
            }
        } else {
            loadNotificationSettings()
        }
    }

    private fun notificationPermissionDenied() {
        setEffect(NotificationSettingsContract.Effect.ShowMessage("Notification permission required"))
    }

    private fun notificationsAllowedChanged(context: Context, enabled: Boolean) {
        if (enabled && ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            setEffect(NotificationSettingsContract.Effect.RequestNotificationPermission)
            return
        }

        val channel = NotificationManagerCompat.from(context).getNotificationChannel(PUSH_NOTIFICATIONS_CHANNEL_ID)

        if (channel?.importance == NotificationManager.IMPORTANCE_NONE) {
            setEffect(NotificationSettingsContract.Effect.ShowMessage("Push notifications disabled in settings"))
            return
        }

        notificationsAllowed = enabled
        updateView()
    }

    private fun loadNotificationSettings() {
        setEffect(NotificationSettingsContract.Effect.ToggleLoader(settings.isNullOrEmpty()))
        launch {
            interactor.getNotificationSettings()
                .onSuccess {
                    settings = it
                    interactor.updateNotificationSettingsDB(it)

                    setEffect(NotificationSettingsContract.Effect.ToggleLoader(false))
                    updateView()
                }
                .onError {
                    setEffect(
                        NotificationSettingsContract.Effect.ToggleLoader(false),
                        NotificationSettingsContract.Effect.ShowMessage(errorHandler.parseError(it))
                    )
                }
        }
    }

    private fun settingChanged(setting: NotificationSettingsModel, enabled: Boolean) {
        settings?.find { it.type == setting.type }?.isEnabled = enabled
        updateView()
    }

    private fun updateView() {
        updateBinding { b ->
            b.notificationsAllowed = notificationsAllowed
            b.settings = settings
        }
    }

    private fun onSaveClick() {
        sharedPreferencesProvider.setNotificationsAllowed(notificationsAllowed)

        if (settings != null && notificationsAllowed) {
            setEffect(NotificationSettingsContract.Effect.ToggleLoader(true))
            launch {
                interactor.updateNotificationSettings(settings!!)
                    .onSuccess {
                        interactor.updateNotificationSettingsDB(settings)

                        setEffect(
                            NotificationSettingsContract.Effect.ToggleLoader(false),
                            NotificationSettingsContract.Effect.ShowMessage(resourceProvider.getString(R.string.saved))
                        )
                    }
                    .onError {
                        setEffect(
                            NotificationSettingsContract.Effect.ToggleLoader(false),
                            NotificationSettingsContract.Effect.ShowMessage(errorHandler.parseError(it))
                        )
                    }
            }
        }
    }
}
