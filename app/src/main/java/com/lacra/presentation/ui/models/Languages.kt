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
package com.lacra.presentation.ui.models

import com.lacra.R

enum class Languages(val languageName: String, val languageCode: String, val languageIcon: Int) {
    ENGLISH("English", "en", languageIcon = R.drawable.ic_en),
    FRENCH("French", "fr", languageIcon = R.drawable.ic_fr),
    SPANISH("Spanish", "es", languageIcon = R.drawable.ic_sp);

    companion object {
        private val codeToLanguageMap = entries.associateBy { it.languageCode }
        private val nameToLanguageMap = entries.associateBy { it.languageName }

        fun fromCode(code: String): Languages? {
            return codeToLanguageMap[code]
        }

        fun fromName(name: String): Languages? {
            return nameToLanguageMap[name]
        }
    }

}