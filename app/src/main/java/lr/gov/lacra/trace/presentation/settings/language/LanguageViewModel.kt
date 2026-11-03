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

import android.content.Context
import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.presentation.ui.models.Languages
import lr.gov.lacra.trace.utils.AppLocaleManager

class LanguageViewModel(
    private val appLocaleManager: AppLocaleManager,
) : BaseViewModel<LanguageContract.Binding>() {

    private var selectedLanguage: String = Languages.ENGLISH.languageCode

    override fun createBinding(): LanguageContract.Binding {
        return LanguageContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is LanguageContract.Event.OnCreate -> onCreate(event.context)
            is LanguageContract.Event.OnSelectedLanguageChange -> onSelectedLanguageChange(event.selectedLanguage)
            is LanguageContract.Event.OnSave -> onSave(event.context)
        }
    }

    override fun copyBinding(binding: LanguageContract.Binding): LanguageContract.Binding {
        return binding.copy()
    }

    private fun updateView() {
        updateBinding { b ->
            b.selectedLanguage = selectedLanguage
        }
    }

    private fun onCreate(context: Context) {
        selectedLanguage = appLocaleManager.getLanguageCode(context)
        updateView()
    }

    private fun onSelectedLanguageChange(selectedLanguage: String) {
        this.selectedLanguage = selectedLanguage
        updateView()
    }

    private fun onSave(context: Context) {
        appLocaleManager.changeLanguage(context, selectedLanguage)
        updateView()
    }
}
