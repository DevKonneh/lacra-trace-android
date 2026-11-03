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

import android.content.Intent
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.ObserveEffects
import lr.gov.lacra.trace.presentation.createtransaction.SharedTransactionViewModel
import lr.gov.lacra.trace.presentation.createtransaction.components.CreateTransactionDialog
import lr.gov.lacra.trace.presentation.createtransaction.components.DashedDivider
import lr.gov.lacra.trace.presentation.main.components.Toolbar2
import lr.gov.lacra.trace.presentation.ui.baseScreen.LoadingButton
import lr.gov.lacra.trace.presentation.ui.components.BaseTextField
import lr.gov.lacra.trace.presentation.ui.components.EmailField
import lr.gov.lacra.trace.presentation.ui.components.PhoneNumberField
import lr.gov.lacra.trace.presentation.ui.components.dialogs.PhoneRegionDialog
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleBodyM
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleBodyS
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme
import lr.gov.lacra.trace.utils.ContactsPermissionRequester
import lr.gov.lacra.trace.utils.ForceUpdateController
import lr.gov.lacra.trace.utils.LocationPermissionRequester
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        UserInfoScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun UserInfoScreen(
    modifier: Modifier,
    navController: NavHostController,
    sharedTransactionViewModel: SharedTransactionViewModel? = null,
    viewModel: UserInfoViewModel? = koinViewModel(),
    isInvite: Boolean = false,
) {
    val binding = viewModel?.observeViewBinding() ?: UserInfoContract.Binding()
    val context = LocalContext.current

    val pickContactLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            viewModel?.setEvent(UserInfoContract.Event.OnContactsPermissionResult(context, it))
        }
    )

    var requestPermission by remember { mutableStateOf(false) }
    var requestContactsPermission by remember { mutableStateOf(false) }
    var showPhoneRegionDialog by remember { mutableStateOf(false) }
    var showUserExistDialog by remember { mutableStateOf(false) }
    var showEmailNotExistDialog by remember { mutableStateOf(false) }
    var showPhoneNotExistDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val forceUpdateController = remember { ForceUpdateController() }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is UserInfoContract.Effect.UserInfoConfirmed -> {
                    sharedTransactionViewModel?.setUserInfo(effect.userInfo)
                    navController.popBackStack()
                }
                is UserInfoContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
                is UserInfoContract.Effect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is UserInfoContract.Effect.ShowUserExist -> {
                    showUserExistDialog = true
                }
                is UserInfoContract.Effect.ShowEmailNotExist -> {
                    showEmailNotExistDialog = true
                }
                is UserInfoContract.Effect.ShowPhoneNotExist -> {
                    showPhoneNotExistDialog = true
                }
                is UserInfoContract.Effect.ConvertToDownstream -> {
                    sharedTransactionViewModel?.setIsProducerTransaction(false)
                    sharedTransactionViewModel?.setUserInfo(effect.userInfo)
                    navController.popBackStack()
                }
                is UserInfoContract.Effect.OpenEmailContacts -> {
                    pickContactLauncher.launch(
                        Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI)
                    )
                }
                is UserInfoContract.Effect.OpenPhoneContacts -> {
                    pickContactLauncher.launch(
                        Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                    )
                }
                is UserInfoContract.Effect.RequestLocationPermission -> {
                    requestPermission = true
                }
                is UserInfoContract.Effect.RequestContactsPermission -> {
                    requestContactsPermission = true
                }
                is UserInfoContract.Effect.ForceUpdateFields -> {
                    forceUpdateController.apply()
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        if (sharedTransactionViewModel != null) {
            viewModel?.setEvent(
                UserInfoContract.Event.OnCreate(context, sharedTransactionViewModel.transaction, isInvite)
            )
        }
    }

    if (requestPermission) {
        LocationPermissionRequester { result ->
            requestPermission = false
            if (result && sharedTransactionViewModel != null) {
                viewModel?.setEvent(
                    UserInfoContract.Event.OnCreate(context, sharedTransactionViewModel.transaction, isInvite)
                )
            }
        }
    }

    if (requestContactsPermission) {
        ContactsPermissionRequester { result ->
            requestContactsPermission = false
            if (result) {
                pickContactLauncher.launch(
                    Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                )
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Toolbar2(
            navController = navController,
            title = binding.title,
        )

        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isInvite) {
                    stringResource(R.string.counterparty_info_instruction)
                } else {
                    stringResource(R.string.farmer_info_instruction)
                },
                style = TextStyleBodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isInvite) {
                BaseTextField(
                    controller = forceUpdateController,

                    labelText = binding.nameLabel,
                    hintText = stringResource(R.string.enter_username),
                    errorText = binding.usernameError,
                    text = binding.username,

                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = R.drawable.ic_user_circle),
                            contentDescription = stringResource(R.string.user_icon),
                        )
                    },

                    onValueChange = {
                        viewModel?.setEvent(UserInfoContract.Event.OnUsernameChanged(it))
                    },
                )
            }

            EmailField(
                controller = forceUpdateController,

                labelText = stringResource(id = R.string.email),
                hintText = stringResource(id = R.string.enter_email),
                errorText = binding.emailError,
                email = binding.email,
                onValueChange = {
                    viewModel?.setEvent(UserInfoContract.Event.OnEmailChanged(it))
                },
                onContactsClicked = {
                    viewModel?.setEvent(UserInfoContract.Event.OnEmailContactsClicked(context))
                }
            )

            if (isInvite) {
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
            }

            PhoneNumberField(
                controller = forceUpdateController,

                labelText = stringResource(id = R.string.phone),
                hintText = stringResource(id = R.string.phone_digits),
                errorText = binding.phoneError,
                phoneRegion = binding.phoneRegion,
                phoneNumber = binding.phoneNumber,
                onValueChange = {
                    viewModel?.setEvent(UserInfoContract.Event.OnPhoneNumberChanged(it))
                },
                onPhoneRegionClicked = {
                    showPhoneRegionDialog = true
                },
                onContactsClicked = {
                    viewModel?.setEvent(UserInfoContract.Event.OnPhoneContactsClicked(context))
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                isLoading = isLoading,
                title = stringResource(R.string.confirm),
                onClick = {
                    viewModel?.setEvent(UserInfoContract.Event.OnConfirm)
                }
            )
        }
    }

    if (showPhoneRegionDialog) {
        PhoneRegionDialog(
            selectedRegion = binding.phoneRegion,
            onDismiss = { showPhoneRegionDialog = false },
            onRegionSelected = {
                showPhoneRegionDialog = false
                viewModel?.setEvent(UserInfoContract.Event.OnPhoneRegionChanged(it))
            }
        )
    }

    if (showUserExistDialog) {
        CreateTransactionDialog(
            title = stringResource(R.string.user_already_exist),
            description = stringResource(R.string.user_already_exist_description),
            actionButtonTitle = stringResource(R.string.continue_downstream),
            secondButtonTitle = stringResource(R.string.continue_initial),
            onActionClick = {
                viewModel?.setEvent(UserInfoContract.Event.OnDownstreamClick)
            },
            onSecondClick = {
                viewModel?.setEvent(UserInfoContract.Event.OnInitialClick)
            },
            onDismiss = {
                showUserExistDialog = false
            }
        )
    }

    if (showEmailNotExistDialog) {
        CreateTransactionDialog(
            title = stringResource(R.string.user_not_exist),
            description = stringResource(R.string.user_email_not_exist),
            actionButtonTitle = stringResource(R.string.continue_anyway),
            secondButtonTitle = stringResource(R.string.cancel),
            onActionClick = {
                viewModel?.setEvent(UserInfoContract.Event.OnInitialClick)
            },
            onSecondClick = {},
            onDismiss = {
                showEmailNotExistDialog = false
            }
        )
    }

    if (showPhoneNotExistDialog) {
        CreateTransactionDialog(
            title = stringResource(R.string.user_not_exist),
            description = stringResource(R.string.user_phone_not_exist),
            actionButtonTitle = stringResource(R.string.continue_anyway),
            secondButtonTitle = stringResource(R.string.cancel),
            onActionClick = {
                viewModel?.setEvent(UserInfoContract.Event.OnInitialClick)
            },
            onSecondClick = {},
            onDismiss = {
                showPhoneNotExistDialog = false
            }
        )
    }
}
