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
package lr.gov.lacra.trace.presentation.settings.changepassword

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.ObserveEffects
import lr.gov.lacra.trace.extensions.findActivity
import lr.gov.lacra.trace.presentation.main.components.Toolbar2
import lr.gov.lacra.trace.presentation.ui.baseScreen.LoadingButton
import lr.gov.lacra.trace.presentation.ui.components.PasswordField
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        ChangePasswordScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun ChangePasswordScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: ChangePasswordViewModel? = koinViewModel()
) {
    val binding = viewModel?.observeViewBinding() ?: ChangePasswordContract.Binding()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is ChangePasswordContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
                is ChangePasswordContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is ChangePasswordContract.Effect.NavigateBack -> {
                    context.findActivity().finish()
                }
            }
        }
    }

    Column(
        modifier = modifier,
    ) {
        Toolbar2(
            navController = navController,
            title = stringResource(id = R.string.create_new_password),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            PasswordField(
                labelText = stringResource(R.string.current_password),
                hintText = stringResource(R.string.enter_current_password),
                errorText = binding.currentPasswordError,
                password = binding.currentPassword,
                onValueChange = {
                    viewModel?.setEvent(ChangePasswordContract.Event.OnCurrentPasswordChanged(it))
                },
            )

            PasswordField(
                labelText = stringResource(R.string.new_password),
                hintText = stringResource(R.string.enter_new_password),
                errorText = binding.newPasswordError,
                password = binding.newPassword,
                onValueChange = {
                    viewModel?.setEvent(ChangePasswordContract.Event.OnNewPasswordChanged(it))
                },
            )

            PasswordField(
                labelText = stringResource(R.string.confirm_new_password),
                hintText = stringResource(R.string.confirm_new_password),
                errorText = binding.confirmPasswordError,
                password = binding.confirmPassword,
                onValueChange = {
                    viewModel?.setEvent(ChangePasswordContract.Event.OnConfirmPasswordChanged(it))
                },
            )

            Spacer(modifier = Modifier.weight(1f))

            LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                isEnabled = binding.saveEnabled,
                isLoading = isLoading,
                title = stringResource(R.string.confirm),
                onClick = {
                    viewModel?.setEvent(ChangePasswordContract.Event.OnSaveClick)
                }
            )
        }
    }
}