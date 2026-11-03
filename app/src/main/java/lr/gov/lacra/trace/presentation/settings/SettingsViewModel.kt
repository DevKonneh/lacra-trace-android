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
package lr.gov.lacra.trace.presentation.settings

import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.data.base.common.onError
import lr.gov.lacra.trace.data.base.common.onSuccess
import lr.gov.lacra.trace.domain.createtransaction.CreateTransactionInteractor
import lr.gov.lacra.trace.domain.settings.SettingsInteractor
import lr.gov.lacra.trace.network.ErrorHandler
import lr.gov.lacra.trace.providers.ResourceProvider
import lr.gov.lacra.trace.providers.TestEnvironmentManager
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val interactor: SettingsInteractor,
    private val createTransactionInteractor: CreateTransactionInteractor,
    private val testEnvironmentManager: TestEnvironmentManager,
    private val errorHandler: ErrorHandler,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<SettingsContract.Binding>() {

    override fun createBinding(): SettingsContract.Binding {
        return SettingsContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is SettingsContract.Event.OnCreate -> updateView()
            is SettingsContract.Event.TestEnvironmentChanged -> onTestEnvironmentChanged(event.isEnabled)
            is SettingsContract.Event.Logout -> logout()
            is SettingsContract.Event.DeleteAccount -> deleteAccount()
        }
    }

    override fun copyBinding(binding: SettingsContract.Binding): SettingsContract.Binding {
        return binding.copy()
    }

    private fun updateView() {
        updateBinding { b ->
            b.isTestEnvironmentEnabled = testEnvironmentManager.isTestEnvironmentEnabled()
        }
    }

    private fun onTestEnvironmentChanged(isEnabled: Boolean) {
        if (isEnabled == testEnvironmentManager.isTestEnvironmentEnabled()) {
            return
        }

        launch {
            if (isEnabled && createTransactionInteractor.getPendingTransactions().isNotEmpty()) {
                setEffect(SettingsContract.Effect.ShowUnsyncedChangesDialog)
                updateView()
                return@launch
            }

            if (!isEnabled) {
                createTransactionInteractor.clearPendingTransactions()
            }

            testEnvironmentManager.setTestEnvironmentEnabled(isEnabled)
            updateView()

            if (isEnabled) {
                setEffect(SettingsContract.Effect.ShowEnteringTestEnvironmentDialog)
            }
        }
    }

    private fun logout() {
        launch(ioContext) {
            interactor.logout()
            setEffect(
                SettingsContract.Effect.ShowMessage(resourceProvider.getString(R.string.logout)),
                SettingsContract.Effect.NavigateAuth
            )
        }
    }

    private fun deleteAccount() {
        setEffect(SettingsContract.Effect.ToggleLoader(true))
        launch {
            interactor.deleteAccount()
                .onSuccess {
                    launch(ioContext) {
                        interactor.logout()
                        setEffect(
                            SettingsContract.Effect.ToggleLoader(false),
                            SettingsContract.Effect.ShowMessage(resourceProvider.getString(R.string.delete_account_success)),
                            SettingsContract.Effect.NavigateAuth
                        )
                    }
                }
                .onError {
                    setEffect(
                        SettingsContract.Effect.ToggleLoader(false),
                        SettingsContract.Effect.ShowMessage(errorHandler.parseError(it))
                    )
                }
        }
    }
}
