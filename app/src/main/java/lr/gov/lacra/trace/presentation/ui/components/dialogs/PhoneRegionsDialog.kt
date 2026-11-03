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
package lr.gov.lacra.trace.presentation.ui.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.presentation.transactions.transactiondetails.components.BaseDialog
import lr.gov.lacra.trace.presentation.ui.theme.ColorSuccess
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme
import lr.gov.lacra.trace.utils.PhoneNumberUtils

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        PhoneRegionDialog(
            selectedRegion = PhoneNumberUtils.getDefaultPhoneRegion(),
        )
    }
}

@Composable
fun PhoneRegionDialog(
    selectedRegion: PhoneNumberUtils.PhoneRegion,
    onDismiss: () -> Unit = {},
    onRegionSelected: (PhoneNumberUtils.PhoneRegion) -> Unit = {},
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCountries by remember(searchQuery) {
        derivedStateOf {
            if (searchQuery.isEmpty()) {
                PhoneNumberUtils.SupportedPhoneRegions
            } else {
                PhoneNumberUtils.SupportedPhoneRegions.filter { phoneRegion ->
                    phoneRegion.countryName.contains(searchQuery, ignoreCase = true) ||
                            phoneRegion.phoneCode.toString().contains(searchQuery, ignoreCase = true) ||
                            phoneRegion.code.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    BaseDialog(
        title = "Select Country",
        onDismiss = onDismiss,
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(stringResource(R.string.search_countries))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            )
        )

        LazyColumn(
            modifier = Modifier.height(400.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = filteredCountries,
                key = { it.code }
            ) { phoneRegion ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onRegionSelected(phoneRegion)
                            searchQuery = ""
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = phoneRegion.flag,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = phoneRegion.countryName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "+${phoneRegion.phoneCode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (selectedRegion.code == phoneRegion.code) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                                tint = ColorSuccess
                            )
                        }
                    }
                }
            }
        }
    }
}