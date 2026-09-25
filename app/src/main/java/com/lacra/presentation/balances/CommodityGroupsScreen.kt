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
package com.lacra.presentation.balances

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.lacra.presentation.balances.components.CommodityGroupList
import com.lacra.presentation.main.components.EmptyState
import com.lacra.presentation.main.components.Toolbar
import com.lacra.presentation.ui.baseScreen.MainIconButton
import com.lacra.presentation.ui.theme.LacraTraceTheme
import com.lacra.utils.setResult
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        CommodityGroupsScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            haveUnreadNotifications = false,
            viewModel = null,
        )
    }
}

@Composable
fun CommodityGroupsScreen(
    modifier: Modifier,
    navController: NavHostController,
    haveUnreadNotifications: Boolean,
    viewModel: CommodityGroupsViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: CommodityGroupsContract.Binding()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                navController.setResult(Screens.Home.route, "createTransactionResult", true)
                navController.popBackStack(Screens.Home.route, false, false)
            }
        }
    )

    var isLoading by remember { mutableStateOf(false) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is CommodityGroupsContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(CommodityGroupsContract.Event.OnCreate)
    }

    Column(
        modifier = modifier
    ) {
        Toolbar(
            title = stringResource(R.string.balances),
//            iconRes = if (haveUnreadNotifications) R.drawable.ic_notification_dot else R.drawable.ic_notification,
        )

        if (!binding.commodities.isNullOrEmpty()) {
            CommodityGroupList(
                modifier = Modifier.fillMaxHeight(),
                sections = binding.commodities!!,
                onSelect = {
                    CommodityGroupBalancesActivity.openCommodityGroupBalances(context, launcher, it)
                }
            )
        } else {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                iconRes = R.drawable.ic_empty,
                title = stringResource(R.string.no_balance),
                description = stringResource(R.string.no_transactions_description),
            ) {
                MainIconButton(
                    modifier = Modifier.padding(top = 24.dp),
                    iconRes = R.drawable.ic_add_transaction,
                    title = stringResource(R.string.add_transaction),
                    onClick = {
                        navController.navigate(Screens.CreateTransaction.route)
                    },
                )
            }
        }
    }
}
