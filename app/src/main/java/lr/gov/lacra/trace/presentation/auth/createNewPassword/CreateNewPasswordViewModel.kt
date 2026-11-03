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
package lr.gov.lacra.trace.presentation.auth.createNewPassword

import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.data.base.common.onError
import lr.gov.lacra.trace.data.base.common.onSuccess
import lr.gov.lacra.trace.domain.auth.AuthInteractor
import lr.gov.lacra.trace.network.ErrorHandler
import lr.gov.lacra.trace.providers.ResourceProvider
import lr.gov.lacra.trace.utils.ValidationUtils

class CreateNewPasswordViewModel(
    private val authInteractor: AuthInteractor,
    private val errorHandler: ErrorHandler,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<CreateNewPasswordContract.Binding>() {

    private var username: String? = null
    private var code: String? = null
    private var password: String = ""
    private var confirmPassword: String = ""
    private var passwordError: String = ""
    private var confirmPasswordError: String = ""

    override fun createBinding(): CreateNewPasswordContract.Binding {
        return CreateNewPasswordContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is CreateNewPasswordContract.Event.OnCreate -> onCreate(event.username, event.code)
            is CreateNewPasswordContract.Event.OnPasswordChanged -> onPasswordChanged(event.password)
            is CreateNewPasswordContract.Event.OnConfirmPasswordChanged -> onConfirmPasswordChanged(event.confirmPassword)
            is CreateNewPasswordContract.Event.OnConfirmClick -> onConfirmClick()
        }
    }

    override fun copyBinding(binding: CreateNewPasswordContract.Binding): CreateNewPasswordContract.Binding {
        return binding.copy()
    }

    private fun updateView() {
        updateBinding { b ->
            b.password = password
            b.confirmPassword = confirmPassword
            b.passwordError = passwordError
            b.confirmPasswordError = confirmPasswordError
            b.confirmEnabled = password.isNotEmpty() && confirmPassword.isNotEmpty()
        }
    }

    private fun onCreate(username: String?, code: String?) {
        this.username = username
        this.code = code
        updateView()
    }

    private fun onPasswordChanged(password: String) {
        this.password = password
        this.passwordError = ""
        this.confirmPasswordError = ""
        updateView()
    }

    private fun onConfirmPasswordChanged(confirmPassword: String) {
        this.confirmPassword = confirmPassword
        this.confirmPasswordError = ""
        updateView()
    }

    private fun onConfirmClick() {
        val passwordValidationStatus = ValidationUtils.validatePassword(password)
        val confirmPasswordValidationStatus = ValidationUtils.validateConfirmPassword(password, confirmPassword)

        passwordError = when(passwordValidationStatus) {
            ValidationUtils.ValidationState.Empty -> ""
            ValidationUtils.ValidationState.Invalid -> resourceProvider.getString(R.string.invalid_password)
            ValidationUtils.ValidationState.Valid -> ""
        }

        confirmPasswordError = when(confirmPasswordValidationStatus) {
            ValidationUtils.ValidationState.Empty -> ""
            ValidationUtils.ValidationState.Invalid -> resourceProvider.getString(R.string.invalid_confirm_password)
            ValidationUtils.ValidationState.Valid -> ""
        }

        if (passwordValidationStatus == ValidationUtils.ValidationState.Valid &&
            confirmPasswordValidationStatus == ValidationUtils.ValidationState.Valid) {

            createNewPass()

        } else {
            updateView()
        }
    }

    private fun createNewPass() {
        if (username != null && code != null) {
            launch {
                setEffect(CreateNewPasswordContract.Effect.ToggleLoader(true))

                authInteractor.passwordResetVerify(username = username, code = code, password = password)
                    .onSuccess {
                        setEffect(
                            CreateNewPasswordContract.Effect.ToggleLoader(false),
                            CreateNewPasswordContract.Effect.NavigateLogin
                        )
                    }
                    .onError {
                        val errorMessage = errorHandler.parseError(it)

                        setEffect(
                            CreateNewPasswordContract.Effect.ToggleLoader(false),
                            CreateNewPasswordContract.Effect.ShowMessage(errorMessage)
                        )
                    }
            }
        } else {
            CreateNewPasswordContract.Effect.ShowMessage("Unexpected error")
        }
    }
}