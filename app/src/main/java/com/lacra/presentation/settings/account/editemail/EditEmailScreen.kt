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
package com.lacra.presentation.settings.account.editemail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.lacra.navigation.Screens
import com.lacra.presentation.main.components.Toolbar2
import com.lacra.presentation.ui.baseScreen.LoadingButton
import com.lacra.presentation.ui.components.EmailField
import com.lacra.presentation.ui.theme.TextStyleBodyM
import com.lacra.presentation.ui.theme.LacraTraceTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        EditEmailScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
            email = null,
        )
    }
}

@Composable
fun EditEmailScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: EditEmailViewModel? = koinViewModel(),
    email: String?,
) {
    val binding = viewModel?.observeViewBinding() ?: EditEmailContract.Binding()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is EditEmailContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
                is EditEmailContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is EditEmailContract.Effect.NavigateToEmailOtp -> {
                    navController.popBackStack()
                    navController.navigate(
                        Screens.ConfirmEmailCodeScreen.putArgs(Screens.ARG_KEY_EMAIL to effect.email)
                    )
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(EditEmailContract.Event.OnCreate(email))
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Toolbar2(
            navController = navController,
            title = stringResource(id = R.string.email_address),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.send_verification_code),
                style = TextStyleBodyM,
                color = MaterialTheme.colorScheme.onSurface,
            )

            EmailField(
                labelText = stringResource(id = R.string.email),
                hintText = stringResource(id = R.string.enter_email),
                errorText = binding.emailError,
                email = binding.email,
                onValueChange = {
                    viewModel?.setEvent(EditEmailContract.Event.OnEmailChanged(it))
                },
            )

            Spacer(modifier = Modifier.weight(1f))

            LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                isEnabled = binding.saveEnabled,
                isLoading = isLoading,
                title = stringResource(R.string.save),
                onClick = {
                    viewModel?.setEvent(EditEmailContract.Event.OnSaveClick)
                }
            )
        }
    }
}
