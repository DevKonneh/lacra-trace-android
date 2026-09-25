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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.navigation.Screens
import com.lacra.presentation.createtransaction.components.CreateTransactionButton1
import com.lacra.presentation.main.components.Toolbar2
import com.lacra.presentation.ui.theme.TextStyleBodyS
import com.lacra.presentation.ui.theme.TextStyleMediumL
import com.lacra.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        CreateTransactionScreen4(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            sharedTransactionViewModel = null,
        )
    }
}

@Composable
fun CreateTransactionScreen4(
    modifier: Modifier,
    navController: NavHostController,
    sharedTransactionViewModel: SharedTransactionViewModel? = null,
) {

    Column(modifier = modifier) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.producer_transaction),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.are_you_on_farm),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyleMediumL,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.farm_geolocation_description),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyleBodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CreateTransactionButton1(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_qr,
                title = stringResource(R.string.yes_on_farm),
            ) {
                sharedTransactionViewModel?.setIsOnFarm(true)
                navController.navigate(Screens.CreateTransactionForm.route)
            }

            CreateTransactionButton1(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_fill_manually,
                title = stringResource(R.string.no_not_on_farm),
            ) {
                sharedTransactionViewModel?.setIsOnFarm(false)
                navController.navigate(Screens.CreateTransactionForm.route)
            }
        }
    }
}