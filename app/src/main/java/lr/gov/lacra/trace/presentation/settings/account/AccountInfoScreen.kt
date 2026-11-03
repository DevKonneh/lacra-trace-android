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
package lr.gov.lacra.trace.presentation.settings.account

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.ObserveEffects
import lr.gov.lacra.trace.navigation.Screens
import lr.gov.lacra.trace.presentation.main.components.Toolbar2
import lr.gov.lacra.trace.presentation.settings.components.AccountItem
import lr.gov.lacra.trace.presentation.settings.components.AccountItem2
import lr.gov.lacra.trace.presentation.ui.baseScreen.LaunchFullScreenLoader
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        AccountInfoScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun AccountInfoScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: AccountInfoViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: AccountInfoContract.Binding()

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is AccountInfoContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
                is AccountInfoContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(AccountInfoContract.Event.OnCreate)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.account_info),
        )
        if (isLoading) {
            LaunchFullScreenLoader()
        } else {
            if (binding.username != null) {
                AccountItem(
                    iconRes = R.drawable.ic_user,
                    title = binding.username!!,
                    description = stringResource(R.string.username_label),
                    onCopyClick = {
                        clipboardManager.setText(AnnotatedString(binding.username!!))
                        Toast.makeText(context, R.string.user_id_copied, Toast.LENGTH_SHORT).show()
                    },
                )
            }

            if (binding.email != null) {
                var onClick: (() -> Unit)? = null
                if (binding.emailEditEnabled) {
                    onClick = {
                        navController.navigate(Screens.EditEmail.putArgs(Screens.ARG_KEY_EMAIL to binding.email!!))
                    }
                }

                AccountItem(
                    iconRes = R.drawable.ic_envelope,
                    title = binding.email!!,
                    description = stringResource(R.string.email_address),
                    warningText = binding.emailWarning,
                    onCopyClick = {
                        clipboardManager.setText(AnnotatedString(binding.email!!))
                        Toast.makeText(context, R.string.email_address_copied, Toast.LENGTH_SHORT).show()
                    },
                    onClick = onClick,
                    onWarningClick = {
                        navController.navigate(
                            Screens.ConfirmEmailCodeScreen.putArgs(Screens.ARG_KEY_EMAIL to binding.email!!)
                        )
                    }
                )
            } else {
                AccountItem2(
                    iconRes = R.drawable.ic_envelope,
                    title = stringResource(R.string.add_email_address),
                    description = stringResource(R.string.email_address_is_missing),
                    onClick = {
                        navController.navigate(Screens.EditEmail.putArgs(Screens.ARG_KEY_EMAIL to ""))
                    }
                )
            }

            if (binding.phone != null && binding.phone!!.length > 1) {
                var onClick: (() -> Unit)? = null
                if (binding.phoneEditEnabled) {
                    onClick = {
                        navController.navigate(Screens.EditPhone.putArgs(Screens.ARG_KEY_PHONE to binding.phone!!))
                    }
                }
                AccountItem(
                    iconRes = R.drawable.ic_phone,
                    title = binding.phone!!,
                    description = stringResource(R.string.phone_number),
                    warningText = binding.phoneWarning,
                    onCopyClick = {
                        clipboardManager.setText(AnnotatedString(binding.phone!!))
                        Toast.makeText(context, R.string.phone_number_copied, Toast.LENGTH_SHORT).show()
                    },
                    onClick = onClick,
                    onWarningClick = {
                        navController.navigate(
                            Screens.ConfirmPhoneCodeScreen.putArgs(Screens.ARG_KEY_PHONE to binding.phone!!)
                        )
                    }
                )
            } else {
                AccountItem2(
                    iconRes = R.drawable.ic_phone,
                    title = stringResource(R.string.add_phone_number),
                    description = stringResource(R.string.phone_number_is_missing),
                    onClick = {
                        navController.navigate(Screens.EditPhone.putArgs(Screens.ARG_KEY_PHONE to ""))
                    }
                )
            }
        }
    }
}
