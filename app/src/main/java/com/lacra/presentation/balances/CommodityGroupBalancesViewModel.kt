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
package com.lacra.presentation.balances

import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.data.base.common.onError
import com.lacra.data.base.common.onSuccess
import com.lacra.domain.commodity.CommodityInteractor
import com.lacra.domain.commodity.models.CommodityFilter
import com.lacra.domain.commodity.models.CommodityGroupModel
import com.lacra.domain.commodity.models.CommodityModel
import com.lacra.network.ErrorHandler

class CommodityGroupBalancesViewModel(
    private val interactor: CommodityInteractor,
    private val errorHandler: ErrorHandler,
) : BaseViewModel<CommodityGroupBalancesContract.Binding>() {

    private var commodityGroup: CommodityGroupModel? = null

    private val filter = CommodityFilter()
    private var commodities: List<CommodityModel>? = null

    override fun createBinding(): CommodityGroupBalancesContract.Binding {
        return CommodityGroupBalancesContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is CommodityGroupBalancesContract.Event.OnCreate -> onCreate(event.commodityGroup)
        }
    }

    override fun copyBinding(binding: CommodityGroupBalancesContract.Binding): CommodityGroupBalancesContract.Binding {
        return binding.copy()
    }

    private fun onCreate(commodityGroup: CommodityGroupModel?) {
        this.commodityGroup = commodityGroup

        if (commodities.isNullOrEmpty()) {
            launch {
                updateCommodities(interactor.getCommoditiesFromDB())
                updateView()

                getCommodities()
            }
        } else {
            getCommodities()
        }
    }

    private fun getCommodities() {
        launch {
            setEffect(CommodityGroupBalancesContract.Effect.ToggleLoader(commodities.isNullOrEmpty()))
            interactor.getCommodities(filter)
                .onSuccess {
                    updateCommodities(it)
                    interactor.updateCommoditiesDB(it)

                    setEffect(CommodityGroupBalancesContract.Effect.ToggleLoader(false))
                    updateView()
                }
                .onError {
                    it.printStackTrace()
                    val errorMessage = errorHandler.parseError(it)
                    setEffect(
                        CommodityGroupBalancesContract.Effect.ToggleLoader(false),
                        CommodityGroupBalancesContract.Effect.ShowMessage(errorMessage)
                    )
                }
        }
    }

    private fun updateCommodities(commodities: List<CommodityGroupModel>?) {
        this.commodities = commodities?.find { it.id == commodityGroup?.id }?.commodities?.filter { it.balance != null }
    }

    private fun updateView() {
        updateBinding { b ->
            b.title = commodityGroup?.name ?: ""
            b.query = filter.query
            b.commodities = commodities
        }
    }
}