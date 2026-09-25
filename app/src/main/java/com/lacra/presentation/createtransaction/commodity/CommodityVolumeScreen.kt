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
package com.lacra.presentation.createtransaction.commodity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.base.ObserveEffects
import com.lacra.domain.createtransaction.models.getCommodityText
import com.lacra.navigation.Screens
import com.lacra.presentation.createtransaction.SharedTransactionViewModel
import com.lacra.presentation.createtransaction.components.CreateTransactionWarning
import com.lacra.presentation.main.components.Toolbar2
import com.lacra.presentation.ui.baseScreen.MainButton
import com.lacra.presentation.ui.components.BaseTextField
import com.lacra.presentation.ui.theme.ColorLightOrange
import com.lacra.presentation.ui.theme.ColorWarning
import com.lacra.presentation.ui.theme.ColorWarning10
import com.lacra.presentation.ui.theme.TextStyleButtonM
import com.lacra.presentation.ui.theme.TextStyleMediumL
import com.lacra.presentation.ui.theme.LacraTraceTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        CommodityVolumeScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            sharedTransactionViewModel = null,
            viewModel = null,
        )
    }
}

@Composable
fun CommodityVolumeScreen(
    modifier: Modifier,
    navController: NavHostController,
    sharedTransactionViewModel: SharedTransactionViewModel? = null,
    viewModel: CommodityVolumeViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: CommodityVolumeContract.Binding()

    var volume by remember { mutableStateOf(sharedTransactionViewModel?.transaction?.volume?.toString()) }

    if (viewModel != null) {
        ObserveEffects(viewModel) { effect ->
            when (effect) {
                is CommodityVolumeContract.Effect.VolumeConfirmed -> {
                    sharedTransactionViewModel?.setVolume(effect.volume)
                    navController.popBackStack()
                }
            }
        }
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        if (sharedTransactionViewModel != null) {
            viewModel?.setEvent(
                CommodityVolumeContract.Event.OnCreate(sharedTransactionViewModel.transaction)
            )
        }
    }

    Column(
        modifier = modifier
    ) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.volume_commodities),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .background(color = MaterialTheme.colorScheme.surface),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = sharedTransactionViewModel?.transaction.getCommodityText() ?: "",
                    modifier = Modifier.weight(1f),
                    style = TextStyleMediumL,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Screens.CommodityTypes.route)
                        },
                    text = stringResource(R.string.edit),
                    style = TextStyleButtonM,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                BaseTextField(
                    modifier = Modifier.weight(2f),
                    hintText = stringResource(R.string.enter_number),
                    labelText = stringResource(R.string.number),
                    text = volume ?: "",
                    warningText = binding.warningText,
                    supportingText = binding.supportingText,
                    onValueChange = {
                        volume = it
                        viewModel?.setEvent(CommodityVolumeContract.Event.OnVolumeChanged(it))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                )

                BaseTextField(
                    modifier = Modifier.weight(1f),
                    isEnabled = false,
                    hintText = sharedTransactionViewModel?.transaction?.commodity?.unit ?: "",
                    labelText = stringResource(R.string.unit),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                )
            }

            if (binding.insufficientBalance) {
                CreateTransactionWarning(
                    modifier = Modifier.padding(16.dp),
                    backgroundColor = ColorLightOrange,
                    borderColor = ColorWarning10,
                    iconRes = R.drawable.ic_warning,
                    iconTint = ColorWarning,
                    title = stringResource(R.string.insufficient_volumes_message),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            MainButton(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                isEnabled = binding.buttonEnabled,
                title = stringResource(R.string.confirm),
                onClick = {
                    viewModel?.setEvent(CommodityVolumeContract.Event.OnConfirm)
                }
            )
        }
    }
}