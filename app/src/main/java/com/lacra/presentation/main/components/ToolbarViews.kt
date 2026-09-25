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
package com.lacra.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lacra.R
import com.lacra.extensions.findActivity
import com.lacra.presentation.ui.theme.TextStyleBodyM
import com.lacra.presentation.ui.theme.TextStyleH1
import com.lacra.presentation.ui.theme.TextStyleH2
import com.lacra.presentation.ui.theme.TextStyleMediumL
import com.lacra.presentation.ui.theme.LacraTraceTheme


@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Toolbar(title = "Test")
            Toolbar(
                title = "Test",
                iconRes = R.drawable.ic_notification_dot,
                onIconClick = {}
            )
            Toolbar2(
                navController = rememberNavController(),
                title = "Test"
            )
            Toolbar2(
                navController = rememberNavController(),
                title = "Test",
                iconRes = R.drawable.ic_notification_dot,
                onIconClick = {}
            )
            Toolbar3(
                title = "Test",
                description = "Test",
            )
        }
    }
}

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    title: String,
    iconRes: Int? = null,
    iconTint: Color = Color.Unspecified,
    onIconClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = TextStyleH2,
            color = MaterialTheme.colorScheme.onSurface,
        )

        if (iconRes != null && onIconClick != null) {
            Icon(
                modifier = Modifier
                    .size(56.dp)
                    .clickable { onIconClick() }
                    .padding(16.dp),
                painter = painterResource(id = iconRes),
                tint = iconTint,
                contentDescription = "Option button",
            )
        }
    }
}

@Composable
fun Toolbar2(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    title: String,
    iconRes: Int? = null,
    iconTint: Color = Color.Unspecified,
    onIconClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier
                .size(56.dp)
                .clickable {
                    if (!navController.popBackStack()) {
                        context
                            .findActivity()
                            .finish()
                    }
                }
                .padding(16.dp),
            painter = painterResource(id = R.drawable.ic_arrow_left),
            contentDescription = "Back button",
        )

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = TextStyleMediumL,
            color = MaterialTheme.colorScheme.onSurface,
        )

        if (iconRes != null && onIconClick != null) {
            Icon(
                modifier = Modifier
                    .size(56.dp)
                    .clickable { onIconClick() }
                    .padding(16.dp),
                painter = painterResource(id = iconRes),
                tint = iconTint,
                contentDescription = "Option button",
            )
        }
    }
}

@Composable
fun Toolbar3(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = TextStyleH1,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = description,
            style = TextStyleBodyM,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}