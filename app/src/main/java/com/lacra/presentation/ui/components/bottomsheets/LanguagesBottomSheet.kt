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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lacra.R
import com.lacra.presentation.settings.components.BottomSheetBase
import com.lacra.presentation.settings.components.SettingsOptionItemBase
import com.lacra.presentation.ui.models.Languages
import com.lacra.presentation.ui.theme.ColorSuccess
import com.lacra.presentation.ui.theme.TextStyleMediumL

@Preview
@Composable
fun LanguagesBottomSheetPreview() {
    LanguagesBottomSheet(
        onDismiss = {},
        onLanguageSelected = {},
    )
}

@Composable
fun LanguagesBottomSheet(
    selectedLanguageCode: String? = null,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit,
) {
    BottomSheetBase(
        onDismissRequest = onDismiss,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.language),
            style = TextStyleMediumL,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {

            items(Languages.entries) {
                LanguageItem(
                    iconRes = it.languageIcon,
                    title = it.languageName,
                    isSelected = it.languageCode == selectedLanguageCode,
                    onClick = {
                        onLanguageSelected(it.languageCode)
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageItem(
    modifier: Modifier = Modifier,
    iconRes: Int,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    SettingsOptionItemBase(
        modifier = modifier,
        iconRes = iconRes,
        iconTint = Color.Unspecified,
        title = title,
        onClick = onClick,
    ) {
        if (isSelected) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = null,
                tint = ColorSuccess
            )
        }
    }
}