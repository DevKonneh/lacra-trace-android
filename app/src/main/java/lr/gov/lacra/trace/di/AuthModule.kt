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
package lr.gov.lacra.trace.di

import lr.gov.lacra.trace.data.auth.repository.AuthRepository
import lr.gov.lacra.trace.data.auth.repository.AuthRepositoryImpl
import lr.gov.lacra.trace.data.auth.service.AuthService
import lr.gov.lacra.trace.data.auth.service.OtpService
import lr.gov.lacra.trace.domain.auth.AuthInteractor
import lr.gov.lacra.trace.domain.auth.AuthInteractorImpl
import lr.gov.lacra.trace.presentation.auth.createNewPassword.CreateNewPasswordViewModel
import lr.gov.lacra.trace.presentation.auth.enterCode.EnterCodeViewModel
import lr.gov.lacra.trace.presentation.auth.forgotPassword.ForgotPasswordViewModel
import lr.gov.lacra.trace.presentation.auth.login.LoginViewModel
import lr.gov.lacra.trace.presentation.auth.registration.RegistrationViewModel
import lr.gov.lacra.trace.presentation.routing.RoutingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val authModule = module {
    // Services
    single { get<Retrofit>(UNAUTHORISED).create(AuthService::class.java) }
    single { get<Retrofit>(UNAUTHORISED).create(OtpService::class.java) }

    // Repositories
    factory<AuthRepository> {
        AuthRepositoryImpl(
            authService = get(),
            preferencesProvider = get(),
            otpService = get()
        )
    }

    // Interactors
    factory<AuthInteractor> { AuthInteractorImpl(get(), get()) }

    // ViewModels
    viewModel { LoginViewModel(get(), get(), get(), get(), get()) }
    viewModel { RegistrationViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { RoutingViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get(), get(), get()) }
    viewModel { CreateNewPasswordViewModel(get(), get(), get()) }
    viewModel { EnterCodeViewModel(get(), get(), get()) }
}
