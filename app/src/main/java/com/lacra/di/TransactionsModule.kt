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
import com.lacra.data.transactions.repository.TransactionsRepository
import com.lacra.data.transactions.repository.TransactionsRepositoryImpl
import com.lacra.data.transactions.service.TransactionsService
import com.lacra.domain.transactions.TransactionDetailsInteractor
import com.lacra.domain.transactions.TransactionDetailsInteractorImpl
import com.lacra.domain.transactions.TransactionsInteractor
import com.lacra.domain.transactions.TransactionsInteractorImpl
import com.lacra.presentation.transactions.TransactionsViewModel
import com.lacra.presentation.transactions.transactiondetails.SuppliersHistoryViewModel
import com.lacra.presentation.transactions.transactiondetails.TransactionDetailsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val transactionsModule = module {
    // Services
    single { get<Retrofit>(AUTHORISED).create(TransactionsService::class.java) }

    // DB
    single { get<AppDatabase>().transactionsDao() }
    single { get<AppDatabase>().traceabilityCountsDao() }

    // Repositories
    factory<TransactionsRepository> { TransactionsRepositoryImpl(service = get(), transactionsDao = get(), traceabilityCountsDao = get()) }

    // Interactors
    factory<TransactionsInteractor> {
        TransactionsInteractorImpl(
            repository = get(),
            context = get(),
            errorHandler = get(),
        )
    }
    factory<TransactionDetailsInteractor> { TransactionDetailsInteractorImpl(repository = get()) }

    // ViewModels
    viewModel {
        TransactionsViewModel(
            allTabInteractor = get(),
            boughtTabInteractor = get(),
            soldTabInteractor = get(),
            createTransactionInteractor = get(),
        )
    }
    viewModel {
        TransactionDetailsViewModel(
            interactor = get(),
            geoDataInteractor = get(),
            resourceProvider = get(),
            errorHandler = get(),
            sharedPreferencesProvider = get(),
        )
    }
    viewModel {
        SuppliersHistoryViewModel(
            interactor = get(),
            geoDataInteractor = get(),
            errorHandler = get(),
            resourceProvider = get(),
        )
    }
}