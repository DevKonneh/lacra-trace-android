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

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import com.lacra.navigation.Screens
import com.lacra.presentation.main.components.Toolbar2
import com.lacra.presentation.ui.baseScreen.LoadingButton
import com.lacra.presentation.ui.components.EmailField
import com.lacra.presentation.ui.components.PhoneNumberField
import com.lacra.presentation.ui.components.dialogs.PhoneRegionDialog
import com.lacra.presentation.ui.theme.TextStyleBodyM
import com.lacra.presentation.ui.theme.TextStyleButtonM
import com.lacra.presentation.ui.theme.LacraTraceTheme
import com.lacra.utils.LocationPermissionRequester
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun ResetPasswordScreenPreview() {
    LacraTraceTheme {
        ForgotPasswordScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier,
    navController: NavHostController,
    email: String? = null,
    phone: String? = null,
    viewModel: ForgotPasswordViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: ForgotPasswordContract.Binding()
    val context = LocalContext.current

    var requestPermission by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showPhoneRegionDialog by remember { mutableStateOf(false) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is ForgotPasswordContract.Effect.RequestLocationPermission -> {
                    requestPermission = true
                }
                is ForgotPasswordContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
                is ForgotPasswordContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is ForgotPasswordContract.Effect.NavigateToEmailOtp -> {
                    navController.navigate(
                        Screens.ForgotPasswordConfirmEmailCodeScreen.putArgs(Screens.ARG_KEY_EMAIL to effect.email)
                    )
                }
                is ForgotPasswordContract.Effect.NavigateToPhoneOtp -> {
                    navController.navigate(
                        Screens.ForgotPasswordConfirmPhoneCodeScreen.putArgs(Screens.ARG_KEY_PHONE to effect.phone)
                    )
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(ForgotPasswordContract.Event.OnCreate(context, email, phone))
    }

    if (requestPermission) {
        LocationPermissionRequester { result ->
            requestPermission = false
            if (result) {
                viewModel?.setEvent(ForgotPasswordContract.Event.OnCreate(context, email, phone))
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Toolbar2(
            navController = navController,
            title = stringResource(id = R.string.forgot_password),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = binding.description,
                style = TextStyleBodyM,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(modifier = Modifier.height(0.dp))

            when (binding.state) {
                ForgotPasswordScreenState.Email -> {
                    EmailField(
                        labelText = stringResource(id = R.string.email),
                        hintText = stringResource(id = R.string.enter_email),
                        errorText = binding.emailError,
                        email = binding.email,
                        onValueChange = {
                            viewModel?.setEvent(ForgotPasswordContract.Event.OnEmailChanged(it))
                        },
                    )
                }
                ForgotPasswordScreenState.Phone -> {
                    PhoneNumberField(
                        labelText = stringResource(id = R.string.phone),
                        hintText = stringResource(id = R.string.phone_digits),
                        errorText = binding.phoneError,
                        phoneRegion = binding.phoneRegion,
                        phoneNumber = binding.phoneNumber,
                        onValueChange = {
                            viewModel?.setEvent(ForgotPasswordContract.Event.OnPhoneChanged(it))
                        },
                        onPhoneRegionClicked = {
                            showPhoneRegionDialog = true
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                isEnabled = binding.sendCodeEnabled,
                isLoading = isLoading,
                title = stringResource(R.string.send_code),
                onClick = {
                    viewModel?.setEvent(ForgotPasswordContract.Event.OnSendCodeClick)
                }
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier
                        .clickable {
                            viewModel?.setEvent(ForgotPasswordContract.Event.OnChangeStatusClick)
                        },
                    text = binding.changeStatusButtonTitle,
                    style = TextStyleButtonM,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }

    if (showPhoneRegionDialog) {
        PhoneRegionDialog(
            selectedRegion = binding.phoneRegion,
            onDismiss = { showPhoneRegionDialog = false },
            onRegionSelected = {
                showPhoneRegionDialog = false
                viewModel?.setEvent(ForgotPasswordContract.Event.OnPhoneRegionChanged(it))
            }
        )
    }
}