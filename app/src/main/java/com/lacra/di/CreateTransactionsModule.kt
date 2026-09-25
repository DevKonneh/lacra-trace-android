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
package com.lacra.di

import com.lacra.data.AppDatabase
import com.lacra.data.createtransaction.repository.CreateTransactionRepository
import com.lacra.data.createtransaction.repository.CreateTransactionRepositoryImpl
import com.lacra.data.createtransaction.repository.UserInfoRepository
import com.lacra.data.createtransaction.repository.UserInfoRepositoryImpl
import com.lacra.data.createtransaction.service.CreateTransactionService
import com.lacra.data.createtransaction.service.UserInfoService
import com.lacra.domain.createtransaction.CreateTransactionInteractor
import com.lacra.domain.createtransaction.CreateTransactionInteractorImpl
import com.lacra.domain.createtransaction.UserInfoInteractor
import com.lacra.domain.createtransaction.UserInfoInteractorImpl
import com.lacra.presentation.createtransaction.SharedTransactionViewModel
import com.lacra.presentation.createtransaction.geodata.qr.QrScanViewModel
import com.lacra.presentation.createtransaction.transactionform.CreateTransactionFormViewModel
import com.lacra.presentation.createtransaction.geodata.uploadfile.UploadFileViewModel
import com.lacra.presentation.createtransaction.userinfo.UserInfoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val createTransactionsModule = module {
    // Services
    single { get<Retrofit>(AUTHORISED).create(CreateTransactionService::class.java) }
    single { get<Retrofit>(AUTHORISED).create(UserInfoService::class.java) }

    // DB
    single { get<AppDatabase>().pendingTransactionsDao() }

    // Repositories
    factory<CreateTransactionRepository> { CreateTransactionRepositoryImpl(service = get(), dao = get(), context = androidContext()) }
    factory<UserInfoRepository> { UserInfoRepositoryImpl(service = get()) }

    // Interactors
    factory<CreateTransactionInteractor> { CreateTransactionInteractorImpl(repository = get(), context = androidContext()) }
    factory<UserInfoInteractor> { UserInfoInteractorImpl(repository = get(), context = androidContext()) }

    // ViewModels
    viewModel { SharedTransactionViewModel() }
    viewModel { UploadFileViewModel(resourceProvider = get(), interactor = get()) }
    viewModel { QrScanViewModel(resourceProvider = get(), interactor = get(), errorHandler = get()) }
    viewModel { CreateTransactionFormViewModel(resourceProvider = get(), interactor = get(), errorHandler = get()) }
    viewModel { UserInfoViewModel(resourceProvider = get(), interactor = get(), errorHandler = get()) }
}