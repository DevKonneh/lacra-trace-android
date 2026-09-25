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
package com.lacra.presentation.settings.notificationsettings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.base.ObserveEffects
import com.lacra.domain.notifications.models.NotificationType
import com.lacra.presentation.main.components.Toolbar2
import com.lacra.presentation.settings.components.SwitchItem1
import com.lacra.presentation.settings.components.SwitchItem2
import com.lacra.presentation.ui.baseScreen.LoadingButton
import com.lacra.presentation.ui.theme.LacraTraceTheme
import com.lacra.utils.PostNotificationPermissionRequester
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        NotificationSettingsScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun NotificationSettingsScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: NotificationSettingsViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: NotificationSettingsContract.Binding()

    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }
    var requestPermission by remember { mutableStateOf(false) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is NotificationSettingsContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
                is NotificationSettingsContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is NotificationSettingsContract.Effect.RequestNotificationPermission -> {
                    requestPermission = true
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(NotificationSettingsContract.Event.OnCreate)
    }

    if (requestPermission) {
        PostNotificationPermissionRequester { result ->
            requestPermission = false

            if (result) {
                viewModel?.setEvent(
                    NotificationSettingsContract.Event.NotificationsAllowedChanged(context, true)
                )
            } else {
                viewModel?.setEvent(NotificationSettingsContract.Event.NotificationPermissionDenied)
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.notifications),
        )

        SwitchItem1(
            title = stringResource(R.string.allow_push_notifications),
            description = stringResource(R.string.allow_push_notifications_description),
            isChecked = binding.notificationsAllowed,
            onChecked = { checked ->
                viewModel?.setEvent(
                    NotificationSettingsContract.Event.NotificationsAllowedChanged(context, checked)
                )
            }
        )

        if (binding.notificationsAllowed && binding.settings != null) {
            LazyColumn(
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(binding.settings!!) {
                    val title = when (it.type) {
                        NotificationType.TransactionPending -> stringResource(R.string.notif_type_transaction_request)
                        NotificationType.TransactionAccepted -> stringResource(R.string.notif_type_transaction_accepted)
                        NotificationType.TransactionRejected -> stringResource(R.string.notif_type_transaction_rejected)
                        NotificationType.TransactionExpired -> stringResource(R.string.notif_type_transaction_expiration)
                        NotificationType.GeoDataMissing -> stringResource(R.string.notif_type_location_request)
                        NotificationType.GeoDataUpdated -> stringResource(R.string.notif_type_location_updated)
                    }

                    SwitchItem2(
                        title = title,
                        isChecked = it.isEnabled,
                        onChecked = { checked ->
                            viewModel?.setEvent(
                                NotificationSettingsContract.Event.SettingChanged(it, checked)
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        LoadingButton(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            isLoading = isLoading,
            title = stringResource(R.string.save),
            onClick = {
                viewModel?.setEvent(NotificationSettingsContract.Event.OnSaveClicked)
            }
        )
    }
}