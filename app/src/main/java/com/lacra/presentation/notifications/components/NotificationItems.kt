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
package com.lacra.presentation.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lacra.R
import com.lacra.presentation.ui.theme.TextStyleBodyM
import com.lacra.presentation.ui.theme.TextStyleBodyS
import com.lacra.presentation.ui.theme.TextStyleButtonM
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
            NotificationItemBase(
                title = "Notification title",
                description = "Notification description in one,\n2 or more lines",
                date = "1h ago",
            )

            NotificationItemBase(
                title = stringResource(R.string.transaction_approval_request),
                description = "1801 Cocoa beans, whole or broken, raw or roasted, 300kg",
                date = "1d ago",
            ) {
            }
        }
    }
}

@Composable
fun NotificationItemBase(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    date: String,
    onDetailsClick: (() -> Unit)? = null,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
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

            if (onDetailsClick != null) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier
                        .clickable {
                            onDetailsClick()
                        },
                    text = stringResource(R.string.view_details),
                    style = TextStyleButtonM,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Text(
            text = date,
            style = TextStyleBodyS,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}