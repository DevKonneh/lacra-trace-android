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
package lr.gov.lacra.trace.presentation.routing

import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.base.CoreViewModel
import lr.gov.lacra.trace.providers.SharedPreferencesProvider
import kotlinx.coroutines.delay

class RoutingViewModel(
    private val sharedPreferencesProvider: SharedPreferencesProvider
) : CoreViewModel<RoutingContract.Binding>() {

    init {
        initBinding(RoutingContract.Binding)
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is RoutingContract.Event.OnCreate -> {
                checkAuth()
            }
        }
    }

    override fun copyBinding(binding: RoutingContract.Binding): RoutingContract.Binding {
        return RoutingContract.Binding
    }

    private fun checkAuth() {
        launch {
            delay(2000)
            if (sharedPreferencesProvider.isAuthorized()) {
                setEffect(RoutingContract.Effect.NavigateMain)
            } else {
                setEffect(RoutingContract.Effect.NavigateAuth)
            }
        }
    }

}