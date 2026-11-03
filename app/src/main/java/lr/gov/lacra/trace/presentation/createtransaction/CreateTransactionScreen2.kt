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
package lr.gov.lacra.trace.presentation.createtransaction

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
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.navigation.Screens
import lr.gov.lacra.trace.presentation.createtransaction.components.CreateTransactionButton3
import lr.gov.lacra.trace.presentation.createtransaction.components.CreateTransactionButton5
import lr.gov.lacra.trace.presentation.createtransaction.components.CreateTransactionButton6
import lr.gov.lacra.trace.presentation.main.components.Toolbar2
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleMediumL
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        CreateTransactionScreen2(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController()
        )
    }
}

@Composable
fun CreateTransactionScreen2(
    modifier: Modifier,
    navController: NavHostController,
    sharedTransactionViewModel: SharedTransactionViewModel? = null,
) {

    Column(modifier = modifier) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.buy_commodity),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.what_transaction_are_you_creating),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyleMediumL,
                color = MaterialTheme.colorScheme.onSurface
            )

            CreateTransactionButton6(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_cube,
                title = stringResource(R.string.seller_not_using_app_description),
                description = stringResource(R.string.seller_not_using_app_description2),
            ) {
                sharedTransactionViewModel?.setIsProducerTransaction(true)
                navController.navigate(Screens.CreateTransaction3.route)
            }

            CreateTransactionButton6(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.ic_arrows,
                title = stringResource(R.string.seller_using_app_description),
                description = stringResource(R.string.seller_using_app_description2),
            ) {
                sharedTransactionViewModel?.setIsProducerTransaction(false)
                navController.navigate(Screens.CreateTransactionForm.route)
            }
        }
    }
}
