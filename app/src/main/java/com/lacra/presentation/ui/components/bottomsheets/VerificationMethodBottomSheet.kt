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
package com.lacra.presentation.ui.components.bottomsheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lacra.R
import com.lacra.presentation.settings.components.BottomSheetBase
import com.lacra.presentation.settings.components.SettingsOptionItemBase

@Preview
@Composable
fun VerificationMethodBottomSheetPreview() {
    VerificationMethodBottomSheet(
        onDismissRequest = {},
        onEmailMethodSelected = {},
        onPhoneMethodSelected = {},
    )
}

@Composable
fun VerificationMethodBottomSheet(
    onDismissRequest: () -> Unit,
    onEmailMethodSelected: () -> Unit,
    onPhoneMethodSelected: () -> Unit,
) {
    BottomSheetBase(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            SettingsOptionItemBase(
                iconRes = R.drawable.ic_envelope,
                title = stringResource(R.string.get_verification_code_with_email),
                onClick = onEmailMethodSelected,
            )

            SettingsOptionItemBase(
                iconRes = R.drawable.ic_phone,
                title = stringResource(R.string.get_verification_code_with_phone),
                onClick = onPhoneMethodSelected,
            )
        }
    }
}