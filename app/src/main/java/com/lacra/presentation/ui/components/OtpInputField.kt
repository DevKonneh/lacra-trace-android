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
package com.lacra.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OtpInputField(
    modifier: Modifier = Modifier,
    otpText: String,
    onOtpChange: (String) -> Unit,
    error: String,
) {
    val otpLength = 6
    val boxSize = 48.dp
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }
    val textFieldStates = remember { List(otpLength) { mutableStateOf(TextFieldValue("")) } }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(otpText) {
        if (otpText.isNotEmpty()) {
            otpText.forEachIndexed { index, char ->
                if (index < otpLength) {
                    textFieldStates[index].value = TextFieldValue(char.toString())
                }
            }
        }
    }
    val isAllFieldsFilled = textFieldStates.all { it.value.text.isNotEmpty() }
    LaunchedEffect(isAllFieldsFilled) {
        val currentOtp = textFieldStates.map { it.value.text }.joinToString("")
        if (currentOtp.isNotEmpty()) {
            onOtpChange(currentOtp)
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            repeat(otpLength) { index ->
                val textFieldValue = textFieldStates[index].value

                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { newValue ->
                        if (newValue.text.length > 1 && textFieldValue.text.isEmpty() && newValue.text.all { it.isDigit() }) {
                            // This condition suggests a paste operation because more than one digit appeared when the field was empty.
                            val pastedText = newValue.text
                            val otpToFill = pastedText.take(otpLength).filter { it.isDigit() }

                            otpToFill.forEachIndexed { charIndex, char ->
                                if (index + charIndex < otpLength) {
                                    textFieldStates[index + charIndex].value =
                                        TextFieldValue(text = char.toString())
                                }
                            }

                            // Move focus to the last filled field or the next available field
                            val lastFilledIndex =
                                (index + otpToFill.length - 1).coerceAtMost(otpLength - 1)
                            if (lastFilledIndex >= 0) {
                                coroutineScope.launch {
                                    // delays is needed before the focusRequesters because it causes to crashes without that
                                    delay(50)
                                    focusRequesters[lastFilledIndex].requestFocus()
                                }
                            }

                        } else if (newValue.text.length <= 1 && newValue.text.all { it.isDigit() }) {
                            textFieldStates[index].value = newValue
                            if (newValue.text.isNotEmpty() && textFieldValue.text.isEmpty() && index < otpLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(boxSize)
                        .clip(MaterialTheme.shapes.small)
                        .border(
                            width = 1.dp,
                            color = if (error.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                            shape = MaterialTheme.shapes.small
                        )
                        .background(
                            if (textFieldValue.text.isNotEmpty()) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.background
                        )
                        .focusRequester(focusRequesters[index])
                        .onKeyEvent { keyEvent ->
                            when (keyEvent.key) {
                                Key.Backspace -> {
                                    val currentValue = textFieldStates[index].value

                                    if (currentValue.text.isEmpty()) {
                                        if (index > 0) {
                                            val previousIndex = index - 1
                                            textFieldStates[previousIndex].value =
                                                TextFieldValue(text = "")
                                            coroutineScope.launch {
                                                delay(30)
                                                focusRequesters[previousIndex].requestFocus()
                                            }
                                        }
                                        true
                                    } else {
                                        textFieldStates[index].value = TextFieldValue(text = "")
                                        true
                                    }
                                }

                                else -> {
                                    false
                                }
                            }
                        },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (textFieldValue.text.isEmpty()) {
                                innerTextField()
                            } else {
                                Text(
                                    text = textFieldValue.text,
                                    style = TextStyle(
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }

        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 16.dp)
            )
        }
    }
}