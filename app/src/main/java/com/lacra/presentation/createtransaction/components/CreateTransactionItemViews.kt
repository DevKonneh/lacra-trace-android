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
package com.lacra.presentation.createtransaction.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lacra.R
import com.lacra.presentation.ui.theme.TextStyleBodyS
import com.lacra.presentation.ui.theme.TextStyleButtonM
import com.lacra.presentation.ui.theme.TextStyleMediumM
import com.lacra.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            CreateTransactionWarning(
                iconRes = R.drawable.ic_information,
                title = stringResource(R.string.please_provide_accurate_info),
            )

            CreateTransactionItem(
                iconRes = R.drawable.ic_map_marker,
                title = stringResource(R.string.farm_geodata),
                description = "4°56'33.1\"N 12°39'15.5\"E",
            )

            CreateTransactionItem(
                iconRes = R.drawable.ic_commodity_type,
                title = stringResource(R.string.commodity_type_required),
                description = stringResource(R.string.tap_to_add_data),
            )

            CreateTransactionItem(
                iconRes = R.drawable.ic_add_commodity,
                title = stringResource(R.string.volume_commodities_required),
                description = stringResource(R.string.tap_to_add_data),
            )

            CreateTransactionItem(
                iconRes = R.drawable.ic_user,
                title = stringResource(R.string.farmer_information),
                description = stringResource(R.string.tap_to_add_data),
            )

            FileItem(
                title = "File_name.csv",
                description = "25kb",
            )
        }
    }
}


@Composable
fun CreateTransactionWarning(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceBright,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    iconRes: Int,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    title: String,
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                style = TextStyleBodyS,
            )
        }
    }
}

@Composable
fun CreateTransactionItem(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

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
                style = TextStyleBodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_chevron_forward),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FileItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_folder),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

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
                style = TextStyleBodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier
                    .clickable {
                        onEditClick()
                    },
                text = stringResource(R.string.upload_another_file),
                style = TextStyleButtonM,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable { onDeleteClick() },
            painter = painterResource(id = R.drawable.ic_trash),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}