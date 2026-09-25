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
import com.lacra.data.commodity.repository.CommodityRepository
import com.lacra.data.commodity.repository.CommodityRepositoryImpl
import com.lacra.data.commodity.repository.ConvertCommodityRepository
import com.lacra.data.commodity.repository.ConvertCommodityRepositoryImpl
import com.lacra.data.commodity.service.CommodityService
import com.lacra.data.commodity.service.ConvertCommodityService
import com.lacra.domain.commodity.CommodityInteractor
import com.lacra.domain.commodity.CommodityInteractorImpl
import com.lacra.domain.commodity.ConvertCommodityInteractor
import com.lacra.domain.commodity.ConvertCommodityInteractorImpl
import com.lacra.presentation.balances.CommodityGroupsViewModel
import com.lacra.presentation.balances.CommodityGroupBalancesViewModel
import com.lacra.presentation.balances.convert.ConvertCommodityViewModel
import com.lacra.presentation.balances.convert.ConvertRecipesViewModel
import com.lacra.presentation.createtransaction.commodity.CommodityTypesViewModel
import com.lacra.presentation.createtransaction.commodity.CommodityVolumeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val commodityModule = module {
    // Services
    single { get<Retrofit>(AUTHORISED).create(CommodityService::class.java) }
    single { get<Retrofit>(AUTHORISED).create(ConvertCommodityService::class.java) }

    // DB
    single { get<AppDatabase>().commodityGroupsDao() }

    // Repositories
    factory<CommodityRepository> { CommodityRepositoryImpl(service = get(), dao = get()) }
    factory<ConvertCommodityRepository> { ConvertCommodityRepositoryImpl(service = get()) }

    // Interactors
    factory<CommodityInteractor> { CommodityInteractorImpl(repository = get()) }
    factory<ConvertCommodityInteractor> { ConvertCommodityInteractorImpl(repository = get(), commodityRepository = get()) }

    // ViewModels
    viewModel { CommodityTypesViewModel(interactor = get(), errorHandler = get()) }
    viewModel { CommodityVolumeViewModel(interactor = get(), errorHandler = get(), resourceProvider = get()) }
    viewModel { CommodityGroupsViewModel(interactor = get(), errorHandler = get()) }
    viewModel { CommodityGroupBalancesViewModel(interactor = get(), errorHandler = get()) }
    viewModel { ConvertRecipesViewModel(interactor = get(), errorHandler = get()) }
    viewModel { ConvertCommodityViewModel(interactor = get(), errorHandler = get()) }
}