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
package com.lacra.presentation.settings.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lacra.R
import com.lacra.presentation.ui.theme.ColorGray10
import com.lacra.presentation.ui.theme.TextStyleBodyM
import com.lacra.presentation.ui.theme.TextStyleMediumM
import com.lacra.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {

            SwitchItem1(
                title = stringResource(R.string.allow_notifications),
                description = stringResource(R.string.allow_notifications_description),
            )

            SwitchItem2(
                title = stringResource(R.string.transaction_request),
                isChecked = true,
            )

            SwitchItem2(
                title = stringResource(R.string.transaction_accepted),
                isChecked = true,
                isEnabled = false
            )
        }
    }
}

@Composable
fun SwitchItem1(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    isChecked: Boolean = false,
    onChecked: (Boolean) -> Unit = {},
    isEnabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = isEnabled,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
            ) {
                onChecked(!isChecked)
            }
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = title,
                style = TextStyleMediumM,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                modifier = modifier.fillMaxWidth(),
                text = description,
                style = TextStyleBodyM,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SwitchBase(
            checked = isChecked,
            enabled = isEnabled,
            onChecked = onChecked,
            interactionSource = interactionSource,
        )
    }
}

@Composable
fun SwitchItem2(
    modifier: Modifier = Modifier,
    title: String,
    isChecked: Boolean = false,
    onChecked: (Boolean) -> Unit = {},
    isEnabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = isEnabled,
                interactionSource = interactionSource,
                indication = LocalIndication.current,
            ) {
                onChecked(!isChecked)
            }
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = TextStyleBodyM,
            color = MaterialTheme.colorScheme.onSurface
        )

        SwitchBase(
            checked = isChecked,
            enabled = isEnabled,
            onChecked = onChecked,
            interactionSource = interactionSource,
        )
    }
}

@Composable
private fun SwitchBase(
    checked: Boolean,
    enabled: Boolean,
    onChecked: (Boolean) -> Unit = {},
    interactionSource: MutableInteractionSource,
) {
    Switch(
        checked = checked,
        onCheckedChange = onChecked,
        thumbContent = {
            Box(modifier = Modifier.size(24.dp))
        },
        enabled = enabled,
        colors = SwitchColors(
            checkedThumbColor = MaterialTheme.colorScheme.surface,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            checkedBorderColor = Color.Transparent,
            checkedIconColor = MaterialTheme.colorScheme.primary,

            uncheckedThumbColor = MaterialTheme.colorScheme.surface,
            uncheckedTrackColor = ColorGray10,
            uncheckedBorderColor = Color.Transparent,
            uncheckedIconColor = ColorGray10,

            disabledCheckedThumbColor = MaterialTheme.colorScheme.surface,
            disabledCheckedTrackColor = ColorGray10,
            disabledCheckedBorderColor = Color.Transparent,
            disabledCheckedIconColor = ColorGray10,

            disabledUncheckedThumbColor = MaterialTheme.colorScheme.surface,
            disabledUncheckedTrackColor = ColorGray10,
            disabledUncheckedBorderColor = Color.Transparent,
            disabledUncheckedIconColor = ColorGray10,
        ),
        interactionSource = interactionSource,
    )
}