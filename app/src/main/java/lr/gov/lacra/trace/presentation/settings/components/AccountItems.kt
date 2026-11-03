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
package lr.gov.lacra.trace.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.presentation.createtransaction.components.CreateTransactionWarning
import lr.gov.lacra.trace.presentation.ui.theme.ColorGray40
import lr.gov.lacra.trace.presentation.ui.theme.ColorGray50
import lr.gov.lacra.trace.presentation.ui.theme.ColorLightOrange
import lr.gov.lacra.trace.presentation.ui.theme.ColorWarning
import lr.gov.lacra.trace.presentation.ui.theme.ColorWarning10
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleBodyS
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleMediumM
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleMediumS
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {

            AccountItem(
                iconRes = R.drawable.ic_user,
                title = "#12351",
                description = stringResource(R.string.username_label),
            )

            AccountItem(
                iconRes = R.drawable.ic_envelope,
                title = "johnsmith@example.com",
                description = stringResource(R.string.email_address),
                warningText = "Email not verified",
                onClick = {}
            )

            AccountItem(
                iconRes = R.drawable.ic_phone,
                title = "+237 125 26 23",
                description = stringResource(R.string.phone_number),
                onClick = {}
            )

            AccountItem2(
                iconRes = R.drawable.ic_envelope,
                title = "Add Email Address",
                description = "Email address is missing",
            )
        }
    }
}

@Composable
fun AccountItem(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    description: String,
    warningText: String? = null,
    onCopyClick: () -> Unit = {},
    onClick: (() -> Unit)? = null,
    onWarningClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick != null) { onClick?.invoke() }
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = ColorGray50,
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

            if (warningText != null) {
                Row(
                    modifier = Modifier
                        .clickable(onWarningClick != null) { onWarningClick?.invoke() }
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_warning),
                        contentDescription = null,
                        tint = ColorWarning
                    )

                    Text(
                        modifier = modifier.fillMaxWidth(),
                        text = warningText,
                        style = TextStyleMediumS,
                        color = ColorWarning
                    )
                }
            }
        }

        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable { onCopyClick() },
            painter = painterResource(id = R.drawable.ic_copy),
            contentDescription = null,
            tint = ColorGray40,
        )

        if (onClick != null) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_chevron_forward),
                contentDescription = null,
                tint = ColorGray40,
            )
        }
    }
}

@Composable
fun AccountItem2(
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
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = ColorGray50,
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                modifier = modifier.fillMaxWidth(),
                text = title,
                style = TextStyleMediumM,
                color = MaterialTheme.colorScheme.primary
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
            tint = ColorGray40,
        )
    }
}