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
package com.lacra.presentation.createtransaction.transactionform

import android.content.Context
import com.lacra.R
import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.data.base.common.onError
import com.lacra.data.base.common.onSuccess
import com.lacra.domain.createtransaction.CreateTransactionInteractor
import com.lacra.domain.createtransaction.models.CreateTransactionModel
import com.lacra.domain.createtransaction.models.getCommodityText
import com.lacra.domain.createtransaction.models.getCommodityVolumeText
import com.lacra.domain.createtransaction.models.getLocationText
import com.lacra.domain.createtransaction.models.getUserInfoText
import com.lacra.domain.transactions.models.TransactionAction
import com.lacra.extensions.toLatLng
import com.lacra.network.ErrorHandler
import com.lacra.providers.ResourceProvider
import com.lacra.utils.checkLocationPermissionGranted
import com.lacra.utils.getCurrentLocation

class CreateTransactionFormViewModel(
    private val resourceProvider: ResourceProvider,
    private val interactor: CreateTransactionInteractor,
    private val errorHandler: ErrorHandler,
) : BaseViewModel<CreateTransactionFormContract.Binding>() {
    private var transaction: CreateTransactionModel? = null

    override fun createBinding(): CreateTransactionFormContract.Binding {
        return CreateTransactionFormContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is CreateTransactionFormContract.Event.OnCreate -> onCreate(event.context, event.transaction)
            is CreateTransactionFormContract.Event.ValidateTransaction -> validateTransaction()
            is CreateTransactionFormContract.Event.CreateTransaction -> createTransaction(event.context)
            is CreateTransactionFormContract.Event.OnFarmGeoDataClick -> onFarmGeoDataClick()
        }
    }

    override fun copyBinding(binding: CreateTransactionFormContract.Binding): CreateTransactionFormContract.Binding {
        return binding.copy()
    }

    private fun onCreate(context: Context, transaction: CreateTransactionModel) {
        this.transaction = transaction

        if (!checkLocationPermissionGranted(context)) {
            setEffect(CreateTransactionFormContract.Effect.RequestLocationPermission)
        }

        updateBinding { b ->
            b.toolbarTitle = if (transaction.isProducerTransaction) {
                resourceProvider.getString(R.string.initial_transaction)
            } else {
                resourceProvider.getString(R.string.downstream_transaction)
            }

            b.farmGeoDataVisible = transaction.isProducerTransaction
            b.farmGeoDataText = transaction.getLocationText() ?: resourceProvider.getString(R.string.tap_to_add_data)

            b.commodityTypeText = transaction.getCommodityText() ?: resourceProvider.getString(R.string.tap_to_add_data)
            b.commodityVolumeText = transaction.getCommodityVolumeText() ?: resourceProvider.getString(R.string.tap_to_add_data)

            b.userInfoVisible = !transaction.isProducerTransaction
            b.userInfoTitle = if (transaction.action == TransactionAction.Buying) {
                resourceProvider.getString(R.string.supplier_information)
            } else {
                resourceProvider.getString(R.string.buyer_information)
            }
            b.userInfoText = transaction.getUserInfoText() ?: resourceProvider.getString(R.string.tap_to_add_data)

            b.inviteUserVisible = transaction.isProducerTransaction && !transaction.isBuyingFromFarmer
            b.inviteUserText = transaction.getUserInfoText() ?:  resourceProvider.getString(R.string.tap_to_invite_user)

            b.buttonEnabled = transaction.commodity != null && transaction.volume != null
        }
    }

    private fun validateTransaction() {
        transaction?.let {
            if (it.isProducerTransaction) {
                setEffect(
                    if (it.locationProvider == null) {
                        CreateTransactionFormContract.Effect.ShowGeoDataDialog
                    } else {
                        CreateTransactionFormContract.Effect.ShowConfirmDialog
                    }
                )
            } else {
                setEffect(CreateTransactionFormContract.Effect.ShowConfirmDialog)
            }
        }
    }

    private fun createTransaction(context: Context) {
        if (transaction != null) {
            launch {
                setEffect(CreateTransactionFormContract.Effect.ToggleLoader(true))

                val location = getCurrentLocation(context)
                transaction = transaction?.copy(creationLocation = location?.toLatLng())

                interactor.createTransaction(transaction!!)
                    .onSuccess {
                        setEffect(
                            CreateTransactionFormContract.Effect.ToggleLoader(false),
                            CreateTransactionFormContract.Effect.CreateTransactionSuccess
                        )

                    }
                    .onError {
                        val errorMessage = errorHandler.parseError(it)
                        setEffect(
                            CreateTransactionFormContract.Effect.ToggleLoader(false),
                            CreateTransactionFormContract.Effect.ShowError(errorMessage)
                        )
                    }
            }
        }
    }

    private fun onFarmGeoDataClick() {
        transaction?.let {
            setEffect(CreateTransactionFormContract.Effect.NavigateFarmGeoData(it))
        }
    }
}