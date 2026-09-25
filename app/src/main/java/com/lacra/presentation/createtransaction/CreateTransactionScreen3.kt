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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.navigation.Screens
import com.lacra.presentation.createtransaction.components.CreateTransactionButton3
import com.lacra.presentation.main.components.Toolbar2
import com.lacra.presentation.ui.theme.TextStyleBodyS
import com.lacra.presentation.ui.theme.TextStyleMediumL
import com.lacra.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        CreateTransactionScreen3(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            sharedTransactionViewModel = null,
        )
    }
}

@Composable
fun CreateTransactionScreen3(
    modifier: Modifier,
    navController: NavHostController,
    sharedTransactionViewModel: SharedTransactionViewModel? = null,
) {

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        if (sharedTransactionViewModel != null) {
            if (!sharedTransactionViewModel.transaction.isProducerTransaction) {
                navController.popBackStack()
            }
        }
    }

    Column(modifier = modifier) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.initial_transaction),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.who_are_you_buying_from),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyleMediumL,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.producer_transaction_description_long),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyleBodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CreateTransactionButton3(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_user_circle,
                title = stringResource(R.string.buying_from_farmer),
                description = stringResource(R.string.buying_from_farmer_description)
            ) {
                sharedTransactionViewModel?.setIsBuyingFromFarmer(true)
                navController.navigate(Screens.CreateTransaction4.route)
            }

            CreateTransactionButton3(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_users,
                title = stringResource(R.string.buying_from_cooperative),
                description = stringResource(R.string.buying_from_cooperative_description)
            ) {
                sharedTransactionViewModel?.setIsBuyingFromFarmer(false)
                navController.navigate(Screens.CreateTransactionForm.route)
            }
        }
    }
}