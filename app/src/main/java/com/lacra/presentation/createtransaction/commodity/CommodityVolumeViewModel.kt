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

import com.lacra.R
import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.domain.commodity.CommodityInteractor
import com.lacra.domain.commodity.models.CommodityBalanceFilter
import com.lacra.domain.createtransaction.models.CreateTransactionModel
import com.lacra.domain.transactions.models.TransactionAction
import com.lacra.network.ErrorHandler
import com.lacra.providers.ResourceProvider

class CommodityVolumeViewModel(
    private val interactor: CommodityInteractor,
    private val errorHandler: ErrorHandler,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<CommodityVolumeContract.Binding>() {

    private var transaction: CreateTransactionModel? = null
    private val filter = CommodityBalanceFilter()
    private var balance: Float? = null
    private var volume: Float? = null

    override fun createBinding(): CommodityVolumeContract.Binding {
        return CommodityVolumeContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is CommodityVolumeContract.Event.OnCreate -> onCreate(event.transaction)
            is CommodityVolumeContract.Event.OnVolumeChanged -> onVolumeChanged(event.volume)
            is CommodityVolumeContract.Event.OnConfirm -> onConfirm()
        }
    }

    override fun copyBinding(binding: CommodityVolumeContract.Binding): CommodityVolumeContract.Binding {
        return binding.copy()
    }

    private fun onCreate(transaction: CreateTransactionModel) {
        this.transaction = transaction

        filter.groupId = transaction.commodity?.group?.id
        filter.commodityId = transaction.commodity?.id

        balance = transaction.commodity?.balance ?: 0f

        updateView()
    }

    private fun updateView() {
        updateBinding { b ->
            b.query = filter.query
            b.volume = volume?.toString()

            val balanceText = transaction?.commodity?.getBalanceText()
            if (balanceText != null) {
                if (balance == null || balance == 0f) {
                    b.supportingText = ""
                    b.warningText = resourceProvider.getString(R.string.your_balance, balanceText)
                } else {
                    b.supportingText = resourceProvider.getString(R.string.your_balance, balanceText)
                    b.warningText = ""
                }
            } else {
                b.supportingText = ""
                b.warningText = ""
            }

            if (transaction?.action == TransactionAction.Buying) {
                b.insufficientBalance = false
            } else {
                b.insufficientBalance = balance != null && volume != null && volume!! > 0 && balance!! < volume!!
            }

            b.buttonEnabled = transaction?.commodity != null && volume != null && volume!! > 0
        }
    }

    private fun onVolumeChanged(volume: String?) {
        this.volume = volume?.toFloatOrNull()

        updateView()
    }

    private fun onConfirm() {
        setEffect(CommodityVolumeContract.Effect.VolumeConfirmed(volume))
    }
}