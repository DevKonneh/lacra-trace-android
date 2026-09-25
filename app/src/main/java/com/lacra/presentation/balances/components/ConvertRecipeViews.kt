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
package com.lacra.presentation.balances.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lacra.presentation.createtransaction.components.dashedBorder
import com.lacra.presentation.ui.theme.TextStyleBodyS
import com.lacra.presentation.ui.theme.TextStyleMediumL
import com.lacra.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp),
        ) {
            ConvertRecipeItem(
                modifier = Modifier.fillMaxWidth(),
                title = "Title",
                description = "Description",
            )
        }
    }
}

@Composable
fun ConvertRecipeItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .dashedBorder(1.dp, MaterialTheme.colorScheme.outline, 8.dp)
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {

            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyleMediumL,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = description,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyleBodyS,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}