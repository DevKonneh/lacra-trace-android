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
package com.lacra.presentation.auth.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.base.ObserveEffects
import com.lacra.extensions.findActivity
import com.lacra.navigation.Screens
import com.lacra.presentation.createtransaction.components.DashedDivider
import com.lacra.presentation.main.MainActivity
import com.lacra.presentation.main.components.TabBar
import com.lacra.presentation.main.components.TabItem
import com.lacra.presentation.main.components.Toolbar3
import com.lacra.presentation.ui.baseScreen.GoogleButton
import com.lacra.presentation.ui.baseScreen.LoadingButton
import com.lacra.presentation.ui.components.EmailField
import com.lacra.presentation.ui.components.PasswordField
import com.lacra.presentation.ui.components.PhoneNumberField
import com.lacra.presentation.ui.components.bottomsheets.LanguagesBottomSheet
import com.lacra.presentation.ui.components.dialogs.PhoneRegionDialog
import com.lacra.presentation.ui.models.Languages
import com.lacra.presentation.ui.theme.TextStyleBodyM
import com.lacra.presentation.ui.theme.TextStyleButtonM
import com.lacra.presentation.ui.theme.LacraTraceTheme
import com.lacra.utils.LocationPermissionRequester
import com.lacra.utils.getResult
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun LogInScreenPreview() {
    LacraTraceTheme {
        LoginScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: LoginViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: LoginContract.Binding()
    val context = LocalContext.current

    var requestPermission by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var showLanguageBottomSheet by remember { mutableStateOf(false) }
    var showPhoneRegionDialog by remember { mutableStateOf(false) }

    val clipboardManager = LocalClipboardManager.current

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is LoginContract.Effect.RequestLocationPermission -> {
                    requestPermission = true
                }
                is LoginContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
                is LoginContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is LoginContract.Effect.NavigateRegistration -> {
                    navController.navigate(Screens.RegistrationScreen.route)
                }
                is LoginContract.Effect.NavigateForgotPassword -> {
                    navController.navigate(Screens.ForgotPasswordScreen.route)
                }
                is LoginContract.Effect.NavigateMainActivity -> {
                    MainActivity.openMain(context.findActivity())
                }
                is LoginContract.Effect.NavigateToEmailOtp -> {
                    navController.navigate(
                        Screens.RegistrationConfirmEmailCodeScreen.putArgs(Screens.ARG_KEY_EMAIL to effect.email)
                    )
                }
                is LoginContract.Effect.NavigateToPhoneOtp -> {
                    navController.navigate(
                        Screens.RegistrationConfirmPhoneCodeScreen.putArgs(Screens.ARG_KEY_PHONE to effect.phone)
                    )
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(LoginContract.Event.OnCreate(context))
    }

    if (requestPermission) {
        LocationPermissionRequester { result ->
            requestPermission = false
            if (result) {
                viewModel?.setEvent(LoginContract.Event.OnCreate(context))
            }
        }
    }

    val confirmCodeUsername = navController.getResult<String>("confirmCodeUsername")

    if (confirmCodeUsername != null) {
        viewModel?.setEvent(LoginContract.Event.OnOtpSuccess(confirmCodeUsername))
    }

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState()),
    ) {
        Toolbar3(
            title = stringResource(id = R.string.log_in),
            description = stringResource(id = R.string.log_in_instructions),
        )

        val tabs = LoginTypeTab.entries.mapIndexed { index, tab ->
            TabItem(title = stringResource(tab.tabNameRes))
        }
        val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                val tab = LoginTypeTab.entries[page]
                viewModel?.setEvent(LoginContract.Event.OnTabChanged(tab))
            }
        }

        TabBar(
            pagerState = pagerState,
            tabs = tabs,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            when (LoginTypeTab.entries[pagerState.currentPage]) {
                LoginTypeTab.Email -> {
                    EmailField(
                        labelText = stringResource(id = R.string.email),
                        hintText = stringResource(id = R.string.enter_email),
                        errorText = binding.emailError,
                        email = binding.email,
                        onValueChange = {
                            viewModel?.setEvent(LoginContract.Event.OnEmailChanged(it))
                        },
                    )
                }
                LoginTypeTab.Phone -> {
                    PhoneNumberField(
                        labelText = stringResource(id = R.string.phone),
                        hintText = stringResource(id = R.string.phone_digits),
                        errorText = binding.phoneError,
                        phoneRegion = binding.phoneRegion,
                        phoneNumber = binding.phoneNumber,
                        onValueChange = {
                            viewModel?.setEvent(LoginContract.Event.OnPhoneChanged(it))
                        },
                        onPhoneRegionClicked = {
                            showPhoneRegionDialog = true
                        },
                    )
                }
            }

            PasswordField(
                labelText = stringResource(id = R.string.password),
                hintText = stringResource(id = R.string.enter_password),
                errorText = binding.passwordError,
                password = binding.password,
                onValueChange = {
                    viewModel?.setEvent(LoginContract.Event.OnPasswordChanged(it))
                },
                onForgotPasswordClick = {
                    viewModel?.setEvent(LoginContract.Event.OnForgotPasswordClick)
                }
            )

            LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                isEnabled = binding.loginEnabled,
                isLoading = isLoading,
                title = stringResource(R.string.log_in),
                onClick = {
                    viewModel?.setEvent(LoginContract.Event.OnLoginClick)
                },
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashedDivider(modifier = Modifier.weight(1f))

                Text(
                    text = stringResource(id = R.string.or),
                    style = TextStyleBodyM,
                    color = MaterialTheme.colorScheme.onSurface
                )

                DashedDivider(modifier = Modifier.weight(1f))
            }

            GoogleButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel?.setEvent(
                        LoginContract.Event.OnGoogleClick(context.findActivity())
                    )
                },
                onLongClick = {
                    viewModel?.setEvent(
                        LoginContract.Event.OnGoogleLongClick(context.findActivity())
                    )
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            ) {
                Text(
                    text = stringResource(R.string.have_account),
                    style = TextStyleBodyM,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    modifier = Modifier
                        .clickable {
                            viewModel?.setEvent(LoginContract.Event.OnRegisterClick)
                        },
                    text = stringResource(R.string.register),
                    style = TextStyleButtonM,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    modifier = Modifier.clickable { showLanguageBottomSheet = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_translation),
                        contentDescription = "Language",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = Languages.fromCode(binding.selectedLanguage)?.languageName ?: Languages.ENGLISH.languageName,
                        style = TextStyleButtonM,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showLanguageBottomSheet) {
        LanguagesBottomSheet(
            selectedLanguageCode = binding.selectedLanguage,
            onDismiss = { showLanguageBottomSheet = false },
            onLanguageSelected = {
                viewModel?.setEvent(LoginContract.Event.OnChangeLanguage(context, it))
            }
        )
    }

    if (showPhoneRegionDialog) {
        PhoneRegionDialog(
            selectedRegion = binding.phoneRegion,
            onDismiss = { showPhoneRegionDialog = false },
            onRegionSelected = {
                showPhoneRegionDialog = false
                viewModel?.setEvent(LoginContract.Event.OnPhoneRegionChanged(it))
            }
        )
    }
}