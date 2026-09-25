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
package com.lacra.presentation.createtransaction

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.domain.transactions.models.TransactionAction
import com.lacra.navigation.Screens
import com.lacra.presentation.createtransaction.components.CreateTransactionButton1
import com.lacra.presentation.main.components.Toolbar
import com.lacra.presentation.notifications.NotificationsActivity
import com.lacra.presentation.ui.theme.LacraTraceTheme
import com.lacra.utils.setResult

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        CreateTransactionScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            haveUnreadNotifications = false,
        )
    }
}

@Composable
fun CreateTransactionScreen(
    modifier: Modifier,
    navController: NavHostController,
    haveUnreadNotifications: Boolean,
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                navController.setResult(Screens.Home.route, "createTransactionResult", true)
                navController.popBackStack(Screens.Home.route, false, false)
            }
        }
    )

    Column {
        Toolbar(
            title = stringResource(R.string.add_transaction),
            iconRes = if (haveUnreadNotifications) R.drawable.ic_notification_dot else R.drawable.ic_notification,
        ) {
            NotificationsActivity.openNotifications(navController.context, launcher)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CreateTransactionButton1(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_status_buy,
                title = stringResource(R.string.buy_commodity),
            ) {
                CreateTransactionActivity.openCreateTransaction(navController.context, launcher, TransactionAction.Buying)
            }

            CreateTransactionButton1(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_status_sell,
                title = stringResource(R.string.sell_commodity),
            ) {
                CreateTransactionActivity.openCreateTransaction(navController.context, launcher, TransactionAction.Selling)
            }
        }
    }
}
