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

import lr.gov.lacra.trace.data.AppDatabase
import lr.gov.lacra.trace.data.notifications.repository.NotificationsRepository
import lr.gov.lacra.trace.data.notifications.repository.NotificationsRepositoryImpl
import lr.gov.lacra.trace.data.notifications.repository.PushNotificationsRepository
import lr.gov.lacra.trace.data.notifications.repository.PushNotificationsRepositoryImpl
import lr.gov.lacra.trace.data.notifications.service.NotificationsService
import lr.gov.lacra.trace.data.notifications.service.PushNotificationsService
import lr.gov.lacra.trace.domain.notifications.NotificationsInteractor
import lr.gov.lacra.trace.domain.notifications.NotificationsInteractorImpl
import lr.gov.lacra.trace.domain.notifications.PushNotificationsInteractor
import lr.gov.lacra.trace.domain.notifications.PushNotificationsInteractorImpl
import lr.gov.lacra.trace.presentation.notifications.NotificationsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val notificationsModule = module {
    // Services
    single { get<Retrofit>(AUTHORISED).create(NotificationsService::class.java) }
    single { get<Retrofit>(AUTHORISED).create(PushNotificationsService::class.java) }

    // DB
    single { get<AppDatabase>().notificationsDao() }

    // Repositories
    factory<NotificationsRepository> { NotificationsRepositoryImpl(service = get(), dao = get()) }
    factory<PushNotificationsRepository> { PushNotificationsRepositoryImpl(service = get()) }

    // Interactors
    factory<NotificationsInteractor> { NotificationsInteractorImpl(repository = get(), context = androidContext()) }
    factory<PushNotificationsInteractor> { PushNotificationsInteractorImpl(repository = get()) }

    // ViewModels
    viewModel { NotificationsViewModel(get(), get()) }
}