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
package lr.gov.lacra.trace.presentation.settings.account

import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.data.base.common.onError
import lr.gov.lacra.trace.data.base.common.onSuccess
import lr.gov.lacra.trace.domain.settings.SettingsInteractor
import lr.gov.lacra.trace.domain.settings.models.AccountModel
import lr.gov.lacra.trace.domain.transactions.models.GadgetTypes
import lr.gov.lacra.trace.network.ErrorHandler
import lr.gov.lacra.trace.providers.ResourceProvider
import lr.gov.lacra.trace.providers.SharedPreferencesProvider

class AccountInfoViewModel(
    private val interactor: SettingsInteractor,
    private val errorHandler: ErrorHandler,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<AccountInfoContract.Binding>() {

    private var accountInfo: AccountModel? = null

    override fun createBinding(): AccountInfoContract.Binding {
        return AccountInfoContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is AccountInfoContract.Event.OnCreate -> onCreate()
        }
    }

    override fun copyBinding(binding: AccountInfoContract.Binding): AccountInfoContract.Binding {
        return binding.copy()
    }

    private fun onCreate() {
        if (accountInfo == null) {
            launch {
                accountInfo = sharedPreferencesProvider.getAccount()
                updateView()

                loadAccountInfo()
            }
        } else {
            loadAccountInfo()
        }
    }

    private fun loadAccountInfo() {
        launch {
            setEffect(AccountInfoContract.Effect.ToggleLoader(accountInfo == null))
            interactor.getAccountInfo()
                .onSuccess {
                    accountInfo = it
                    it?.let { it1 -> sharedPreferencesProvider.setAccount(it1) }

                    setEffect(AccountInfoContract.Effect.ToggleLoader(false))
                    updateView()
                }
                .onError {
                    setEffect(
                        AccountInfoContract.Effect.ToggleLoader(false),
                        AccountInfoContract.Effect.ShowMessage(errorHandler.parseError(it))
                    )
                }
        }
    }

    private fun updateView() {
        val email = accountInfo?.gadgets?.find { it.type == GadgetTypes.Email }
        val phone = accountInfo?.gadgets?.find { it.type == GadgetTypes.Phone }

        var phoneText = phone?.identifier
        if (phoneText != null && !phoneText.startsWith("+")) {
            phoneText = "+$phoneText"
        }

        updateBinding { b ->
            b.username = accountInfo?.username

            b.email = email?.identifier
            b.emailEditEnabled = email?.isVerified != true || phone?.isVerified == true
            b.emailWarning = if (email?.isVerified != true) resourceProvider.getString(R.string.email_not_verified) else null

            b.phone = phoneText
            b.phoneEditEnabled = phone?.isVerified != true || email?.isVerified == true
            b.phoneWarning = if (phone?.isVerified != true) resourceProvider.getString(R.string.phone_not_verified) else null
        }
    }
}
