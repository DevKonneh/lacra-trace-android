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
package com.lacra.presentation.createtransaction.commodity

import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.data.base.common.onError
import com.lacra.data.base.common.onSuccess
import com.lacra.domain.commodity.CommodityInteractor
import com.lacra.domain.commodity.models.CommodityFilter
import com.lacra.domain.commodity.models.CommodityGroupModel
import com.lacra.domain.createtransaction.models.CreateTransactionModel
import com.lacra.network.ErrorHandler

class CommodityTypesViewModel(
    private val interactor: CommodityInteractor,
    private val errorHandler: ErrorHandler,
) : BaseViewModel<CommodityTypesContract.Binding>() {

    private var transaction: CreateTransactionModel? = null
    private val filter = CommodityFilter()
    private var commodities: List<CommodityGroupModel>? = null

    override fun createBinding(): CommodityTypesContract.Binding {
        return CommodityTypesContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is CommodityTypesContract.Event.OnCreate -> onCreate(event.transaction)
        }
    }

    override fun copyBinding(binding: CommodityTypesContract.Binding): CommodityTypesContract.Binding {
        return binding.copy()
    }

    private fun onCreate(transaction: CreateTransactionModel) {
        this.transaction = transaction

        if (commodities.isNullOrEmpty()) {
            launch {
                commodities = interactor.getCommoditiesFromDB()
                updateView()

                getCommodities()
            }
        } else {
            getCommodities()
        }
    }

    private fun getCommodities() {
        launch {
            setEffect(CommodityTypesContract.Effect.ToggleLoader(commodities.isNullOrEmpty()))
            interactor.getCommodities(filter)
                .onSuccess {
                    commodities = it
                    interactor.updateCommoditiesDB(it)

                    setEffect(CommodityTypesContract.Effect.ToggleLoader(false))
                    updateView()
                }
                .onError {
                    it.printStackTrace()
                    val errorMessage = errorHandler.parseError(it)
                    setEffect(
                        CommodityTypesContract.Effect.ToggleLoader(false),
                        CommodityTypesContract.Effect.ShowMessage(errorMessage)
                    )
                }
        }
    }

    private fun updateView() {
        updateBinding { b ->
            b.query = filter.query
            b.commodities = commodities
        }
    }
}