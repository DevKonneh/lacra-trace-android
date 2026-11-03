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
package lr.gov.lacra.trace.presentation.createtransaction.commodity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import lr.gov.lacra.trace.presentation.createtransaction.components.CommodityList
import lr.gov.lacra.trace.presentation.main.components.Toolbar2
import lr.gov.lacra.trace.presentation.ui.baseScreen.LoadingButton
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        CommodityTypesScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            sharedTransactionViewModel = null,
            viewModel = null,
        )
    }
}

@Composable
fun CommodityTypesScreen(
    modifier: Modifier,
    navController: NavHostController,
    sharedTransactionViewModel: SharedTransactionViewModel? = null,
    viewModel: CommodityTypesViewModel? = koinViewModel()
) {
    val binding = viewModel?.observeViewBinding() ?: CommodityTypesContract.Binding()

    var selectedCommodity by remember { mutableStateOf(sharedTransactionViewModel?.transaction?.commodity) }
    var isLoading by remember { mutableStateOf(false) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is CommodityTypesContract.Effect.ToggleLoader -> {
                    isLoading = effect.isLoading
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        if (sharedTransactionViewModel != null) {
            viewModel?.setEvent(
                CommodityTypesContract.Event.OnCreate(sharedTransactionViewModel.transaction)
            )
        }
    }

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.commodity_type),
        )

        if (binding.commodities != null) {
            CommodityList(
                modifier = Modifier.weight(1f),
                sections = binding.commodities!!,
                selectedCommodity = selectedCommodity,
            ) {
                selectedCommodity = it
            }

            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                LoadingButton(
                    modifier = Modifier.fillMaxWidth(),
                    isEnabled = selectedCommodity != null,
                    isLoading = isLoading,
                    title = stringResource(R.string.confirm),
                    onClick = {
                        sharedTransactionViewModel?.setCommodity(selectedCommodity)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
