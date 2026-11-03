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

import lr.gov.lacra.trace.data.geodata.repository.GeoDataRepository
import lr.gov.lacra.trace.data.geodata.repository.GeoDataRepositoryImpl
import lr.gov.lacra.trace.data.geodata.service.GeoDataService
import lr.gov.lacra.trace.domain.geodata.GeoDataInteractor
import lr.gov.lacra.trace.domain.geodata.GeoDataInteractorImpl
import lr.gov.lacra.trace.presentation.createtransaction.geodata.FarmGeoDataViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val geoDataModule = module {
    // Services
    single { get<Retrofit>(AUTHORISED).create(GeoDataService::class.java) }

    // Repositories
    factory<GeoDataRepository> { GeoDataRepositoryImpl(service = get(), context = androidContext()) }

    // Interactors
    factory<GeoDataInteractor> { GeoDataInteractorImpl(repository = get()) }

    // ViewModels
    viewModel { FarmGeoDataViewModel() }
}