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
package lr.gov.lacra.trace.presentation.settings.language

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.presentation.main.components.Toolbar2
import lr.gov.lacra.trace.presentation.settings.components.LanguageItem
import lr.gov.lacra.trace.presentation.ui.baseScreen.MainButton
import lr.gov.lacra.trace.presentation.ui.models.Languages
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        LanguageScreen(
            modifier = Modifier.fillMaxSize(),
            navController = rememberNavController(),
            viewModel = null,
        )
    }
}

@Composable
fun LanguageScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: LanguageViewModel? = koinViewModel(),
) {
    val binding = viewModel?.observeViewBinding() ?: LanguageContract.Binding()
    val context = LocalContext.current

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        viewModel?.setEvent(LanguageContract.Event.OnCreate(context))
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Toolbar2(
            navController = navController,
            title = stringResource(R.string.language),
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(Languages.entries.toTypedArray()) {
                LanguageItem(
                    iconRes = it.languageIcon,
                    title = it.languageName,
                    isSelected = binding.selectedLanguage == it.languageCode,
                    onClick = {
                        viewModel?.setEvent(LanguageContract.Event.OnSelectedLanguageChange(it.languageCode))
                    },
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        MainButton(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            title = stringResource(R.string.save),
            onClick = {
                viewModel?.setEvent(LanguageContract.Event.OnSave(context))
            }
        )
    }
}