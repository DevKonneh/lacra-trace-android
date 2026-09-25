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
package com.lacra.presentation.ui.baseScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.lacra.presentation.ui.theme.ColorSeaBlue50
import com.lacra.presentation.ui.theme.TextStyleButtonM
import com.lacra.presentation.ui.theme.TextStyleMediumM
import com.lacra.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            BaseButton1()
            MainIconButton(iconRes = R.drawable.ic_add_transaction, title = "Icon button")
            MainButton(title = "Main button")
            MainButton(isEnabled = false, title = "Main button d")
            LoadingButton(title = "Loading button e")
            LoadingButton(isLoading = true, title = "Loading button e l")
            LoadingButton(isEnabled = false, title = "Loading button d")
            LightLoadingButton(title = "Loading button e")
            LightLoadingButton(isLoading = true, title = "Loading button e l")
            LightLoadingButton(isEnabled = false, title = "Loading button d")

            Spacer(modifier = Modifier.height(24.dp))

            GoogleButton(isLoading = true)
            GoogleButton(isEnabled = false)
        }
    }
}

@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    title: String,
    onClick: () -> Unit = {},
) {
    var enabled = isEnabled
    if (isLoading) enabled = false

    BaseButton1(
        modifier = modifier,
        isEnabled = enabled,
        backgroundColor = MaterialTheme.colorScheme.primary,
        disabledBackgroundColor = ColorSeaBlue50,
        onClick = onClick,
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = title,
                style = TextStyleButtonM,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    var enabled = isEnabled
    if (isLoading) enabled = false

    BaseButton1(
        modifier = modifier,
        isEnabled = enabled,
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outline,
        onClick = onClick,
        onLongClick = onLongClick,
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    tint = Color.Unspecified
                )

                Text(
                    stringResource(R.string.continue_with_google),
                    style = TextStyleMediumM
                )
            }
        }
    }
}

@Composable
fun LightLoadingButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    isLoading: Boolean = false,
    title: String,
    onClick: () -> Unit = {},
) {
    var enabled = isEnabled
    if (isLoading) enabled = false

    BaseButton1(
        modifier = modifier,
        isEnabled = enabled,
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.outline,
        onClick = onClick,
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text(
                text = title,
                style = TextStyleButtonM,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    title: String,
    onClick: () -> Unit = {},
) {
    BaseButton1(
        modifier = modifier,
        isEnabled = isEnabled,
        backgroundColor = MaterialTheme.colorScheme.primary,
        disabledBackgroundColor = ColorSeaBlue50,
        onClick = onClick,
    ) {
        Text(
            text = title,
            style = TextStyleButtonM,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
fun MainIconButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    iconRes: Int,
    title: String,
    onClick: () -> Unit = {},
) {
    BaseButton1(
        modifier = modifier,
        isEnabled = isEnabled,
        backgroundColor = MaterialTheme.colorScheme.primary,
        disabledBackgroundColor = ColorSeaBlue50,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = title,
                style = TextStyleButtonM,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun BaseButton1(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    borderColor: Color = backgroundColor,
    disabledBackgroundColor: Color = backgroundColor,
    disabledBorderColor: Color = disabledBackgroundColor,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .height(48.dp)
                .background(
                    color = if (isEnabled) backgroundColor else disabledBackgroundColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .border(1.dp, if (isEnabled) borderColor else disabledBorderColor, RoundedCornerShape(8.dp))
                .combinedClickable(
                    enabled = isEnabled,
                    onClick = { onClick() },
                    onLongClick = { onLongClick() }
                ),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}