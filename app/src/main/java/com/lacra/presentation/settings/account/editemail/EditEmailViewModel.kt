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
package com.lacra.presentation.settings.account.editemail

import com.lacra.R
import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.data.base.common.onError
import com.lacra.data.base.common.onSuccess
import com.lacra.domain.settings.SettingsInteractor
import com.lacra.network.ErrorHandler
import com.lacra.providers.ResourceProvider
import com.lacra.utils.ValidationUtils

class EditEmailViewModel(
    private val interactor: SettingsInteractor,
    private val errorHandler: ErrorHandler,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<EditEmailContract.Binding>() {

    private var email: String? = null
    private var newEmail: String = ""
    private var emailError: String = ""

    override fun createBinding(): EditEmailContract.Binding {
        return EditEmailContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is EditEmailContract.Event.OnCreate -> onCreate(event.email)
            is EditEmailContract.Event.OnEmailChanged -> onEmailChanged(event.email)
            is EditEmailContract.Event.OnSaveClick -> onSaveClick()
        }
    }

    override fun copyBinding(binding: EditEmailContract.Binding): EditEmailContract.Binding {
        return binding.copy()
    }

    private fun updateView() {
        updateBinding { b ->
            b.email = newEmail
            b.emailError = emailError
        }
    }

    private fun onCreate(email: String?) {
        if (!email.isNullOrEmpty()) {
            this.email = email
            this.newEmail = email
        }

        updateView()

        setEffect(EditEmailContract.Effect.ForceUpdateFields)
    }

    private fun onEmailChanged(email: String) {
        this.newEmail = email
        this.emailError = ""
        updateView()
    }

    private fun onSaveClick() {
        val validationStatus = ValidationUtils.validateEmail(newEmail)

        emailError = when(validationStatus) {
            ValidationUtils.ValidationState.Empty -> "empty"
            ValidationUtils.ValidationState.Invalid -> resourceProvider.getString(R.string.invalid_email)
            ValidationUtils.ValidationState.Valid -> ""
        }

        if (validationStatus == ValidationUtils.ValidationState.Valid) {
            editEmail(email, newEmail)
        } else {
            updateView()
        }
    }

    private fun editEmail(oldEmail: String?, newEmail: String) {
        launch {
            setEffect(EditEmailContract.Effect.ToggleLoader(true))

            if (oldEmail != null) {
                interactor.deleteEmail(oldEmail)
                    .onSuccess {
                        setEffect(EditEmailContract.Effect.ToggleLoader(false))
                        addEmail(newEmail)
                    }
                    .onError {
                        val errorMessage = errorHandler.parseError(it)

                        setEffect(
                            EditEmailContract.Effect.ToggleLoader(false),
                            EditEmailContract.Effect.ShowMessage(errorMessage)
                        )
                    }
            } else {
                addEmail(newEmail)
            }
        }
    }

    private fun addEmail(email: String) {
        launch {
            setEffect(EditEmailContract.Effect.ToggleLoader(true))

            interactor.addEmail(email)
                .onSuccess {
                    setEffect(
                        EditEmailContract.Effect.ToggleLoader(false),
                        EditEmailContract.Effect.NavigateToEmailOtp(email)
                    )
                }
                .onError {
                    val errorMessage = errorHandler.parseError(it)

                    setEffect(
                        EditEmailContract.Effect.ToggleLoader(false),
                        EditEmailContract.Effect.ShowMessage(errorMessage)
                    )
                }
        }
    }
}