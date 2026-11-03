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
package lr.gov.lacra.trace.presentation.settings.changepassword

import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.data.base.common.onError
import lr.gov.lacra.trace.data.base.common.onSuccess
import lr.gov.lacra.trace.domain.settings.SettingsInteractor
import lr.gov.lacra.trace.network.ErrorHandler
import lr.gov.lacra.trace.providers.ResourceProvider
import lr.gov.lacra.trace.utils.ValidationUtils

class ChangePasswordViewModel(
    private val interactor: SettingsInteractor,
    private val errorHandler: ErrorHandler,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<ChangePasswordContract.Binding>() {

    private var currentPassword: String = ""
    private var newPassword: String = ""
    private var confirmPassword: String = ""
    private var currentPasswordError: String = ""
    private var newPasswordError: String = ""
    private var confirmPasswordError: String = ""

    override fun createBinding(): ChangePasswordContract.Binding {
        return ChangePasswordContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        when (event) {
            is ChangePasswordContract.Event.OnCurrentPasswordChanged -> onCurrentPasswordChanged(event.currentPassword)
            is ChangePasswordContract.Event.OnNewPasswordChanged -> onNewPasswordChanged(event.newPassword)
            is ChangePasswordContract.Event.OnConfirmPasswordChanged -> onConfirmPasswordChanged(event.confirmPassword)
            is ChangePasswordContract.Event.OnSaveClick -> onSaveClick()
        }
    }

    override fun copyBinding(binding: ChangePasswordContract.Binding): ChangePasswordContract.Binding {
        return binding.copy()
    }

    private fun updateView() {
        updateBinding { b ->
            b.currentPassword = currentPassword
            b.newPassword = newPassword
            b.confirmPassword = confirmPassword

            b.currentPasswordError = currentPasswordError
            b.newPasswordError = newPasswordError
            b.confirmPasswordError = confirmPasswordError

            b.saveEnabled = currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmPassword.isNotEmpty()
        }
    }

    private fun onCurrentPasswordChanged(currentPassword: String) {
        this.currentPassword = currentPassword
        this.currentPasswordError = ""
        updateView()
    }

    private fun onNewPasswordChanged(newPassword: String) {
        this.newPassword = newPassword
        this.newPasswordError = ""
        this.confirmPasswordError = ""
        updateView()
    }

    private fun onConfirmPasswordChanged(confirmPassword: String) {
        this.confirmPassword = confirmPassword
        this.confirmPasswordError = ""
        updateView()
    }

    private fun onSaveClick() {
        val currentPasswordValidationStatus = ValidationUtils.validatePassword(currentPassword)
        val newPasswordValidationStatus = ValidationUtils.validatePassword(newPassword)
        val confirmPasswordValidationStatus = ValidationUtils.validateConfirmPassword(newPassword, confirmPassword)

        currentPasswordError = when(currentPasswordValidationStatus) {
            ValidationUtils.ValidationState.Empty -> "This field should not be empty"
            ValidationUtils.ValidationState.Invalid -> ""
            ValidationUtils.ValidationState.Valid -> ""
        }

        newPasswordError = when(newPasswordValidationStatus) {
            ValidationUtils.ValidationState.Empty -> ""
            ValidationUtils.ValidationState.Invalid -> resourceProvider.getString(R.string.invalid_password)
            ValidationUtils.ValidationState.Valid -> ""
        }

        confirmPasswordError = when(confirmPasswordValidationStatus) {
            ValidationUtils.ValidationState.Empty -> ""
            ValidationUtils.ValidationState.Invalid -> resourceProvider.getString(R.string.invalid_confirm_password)
            ValidationUtils.ValidationState.Valid -> ""
        }

        if (currentPasswordValidationStatus != ValidationUtils.ValidationState.Empty &&
            newPasswordValidationStatus == ValidationUtils.ValidationState.Valid &&
            confirmPasswordValidationStatus == ValidationUtils.ValidationState.Valid) {

            changePassword()

        } else {
            updateView()
        }
    }

    private fun changePassword() {
        launch {
            setEffect(ChangePasswordContract.Effect.ToggleLoader(true))

            interactor.changePassword(currentPassword, newPassword)
                .onSuccess {
                    setEffect(
                        ChangePasswordContract.Effect.ToggleLoader(false),
                        ChangePasswordContract.Effect.ShowMessage("Password changed"),
                        ChangePasswordContract.Effect.NavigateBack,
                    )
                }
                .onError {
                    val errorMessage = errorHandler.parseError(it)

                    setEffect(
                        ChangePasswordContract.Effect.ToggleLoader(false),
                        ChangePasswordContract.Effect.ShowMessage(errorMessage)
                    )
                }
        }
    }
}

