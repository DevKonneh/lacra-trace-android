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
package com.lacra.presentation.transactions.transactiondetails

import com.lacra.R
import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.data.base.common.onError
import com.lacra.data.base.common.onSuccess
import com.lacra.domain.geodata.GeoDataInteractor
import com.lacra.domain.settings.models.AccountModel
import com.lacra.domain.transactions.TransactionDetailsInteractor
import com.lacra.domain.transactions.models.TraceabilityCountsModel
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.domain.transactions.models.TransactionStatus
import com.lacra.domain.transactions.models.TransactionType
import com.lacra.domain.transactions.models.getCommodityFullText
import com.lacra.extensions.toFormattedDateString
import com.lacra.network.ErrorHandler
import com.lacra.presentation.transactions.transactiondetails.components.PieChartItem
import com.lacra.presentation.ui.theme.ColorMidnightBlue
import com.lacra.presentation.ui.theme.ColorMulberryPurple
import com.lacra.presentation.ui.theme.ColorSeaBlue
import com.lacra.presentation.ui.theme.ColorSuccess
import com.lacra.providers.ResourceProvider
import com.lacra.providers.SharedPreferencesProvider

class TransactionDetailsViewModel(
    private val interactor: TransactionDetailsInteractor,
    private val geoDataInteractor: GeoDataInteractor,
    private val resourceProvider: ResourceProvider,
    private val errorHandler: ErrorHandler,
    private val sharedPreferencesProvider: SharedPreferencesProvider,
) : BaseViewModel<TransactionDetailsContract.Binding>() {

    private var transactionId: String = ""
    private var transactionModel: TransactionModel? = null
    private var accountModel: AccountModel? = null
    private var traceabilityCounts: TraceabilityCountsModel? = null
    private var statusUpdated = false

    override fun createBinding(): TransactionDetailsContract.Binding {
        return TransactionDetailsContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is TransactionDetailsContract.Event.OnCreate -> onCreate(event.transactionModel)
            is TransactionDetailsContract.Event.Refresh -> refresh()
            is TransactionDetailsContract.Event.AddGeoDataClicked -> addGeoDataClicked()
            is TransactionDetailsContract.Event.AcceptTransaction -> acceptTransaction()
            is TransactionDetailsContract.Event.RejectTransaction -> rejectTransaction()
            is TransactionDetailsContract.Event.ResendNotification -> resendNotification()
            is TransactionDetailsContract.Event.OnInitialSuppliersHistoryClick -> onInitialSuppliersHistoryClick()
            is TransactionDetailsContract.Event.OnSuppliersHistoryClick -> onSuppliersHistoryClick(event.transaction)
        }
    }

    override fun copyBinding(binding: TransactionDetailsContract.Binding): TransactionDetailsContract.Binding {
        return binding.copy()
    }

    private fun onCreate(transaction: TransactionModel) {
        transactionId = transaction.id
        accountModel = sharedPreferencesProvider.getAccount()

        launch {
            transactionModel = interactor.getTransactionDetailsFromDB(transactionId)
            traceabilityCounts = interactor.getTraceabilityCountsFromDB(transactionId)

            if (transactionModel == null) transactionModel = transaction

            updateView()
            updateChartItemsView()

            refresh()

            setEffect(TransactionDetailsContract.Effect.RefreshHistory(transactionModel!!))
        }
    }

    private fun refresh() {
        getTransactionDetails()
        getTransactionTraceability()
    }

    private fun getTransactionDetails() {
        launch {
            interactor.getTransactionDetails(transactionId)
                .onSuccess {
                    if (it != null) {
                        transactionModel = it
                        interactor.saveTransactionDetailsToDB(it)

                        if (statusUpdated) {
                            statusUpdated = false
                            setEffect(TransactionDetailsContract.Effect.RefreshHistory(it))
                        }

                        getTransactionTraceability()

                        updateView()
                    }
                }
                .onError {
                    val errorMessage = errorHandler.parseError(it)
                }
        }
    }

    private fun getTransactionTraceability() {
        if (transactionModel?.traceability != null
            && (transactionModel?.status == TransactionStatus.Accepted
                    || transactionModel?.status == TransactionStatus.Recorded)) {
            launch {
                interactor.getTransactionTraceability(transactionId)
                    .onSuccess {
                        if (it != null) {
                            traceabilityCounts = it
                            interactor.saveTraceabilityCountsToDB(transactionId, it)

                            updateChartItemsView()
                        }
                    }
                    .onError {
                        val errorMessage = errorHandler.parseError(it)
                    }
            }
        }
    }

    private fun updateView() {
        updateBinding { b ->
            b.commodityText = transactionModel?.getCommodityFullText()

            b.showLocation = transactionModel?.type == TransactionType.Producer || transactionModel?.isAutomatic == true
            b.locationProvider = transactionModel?.locationProvider
            b.location = transactionModel?.location

            b.traceability = transactionModel?.traceability

            b.accountId = accountModel?.id
            b.buyer = transactionModel?.buyer
            b.seller = transactionModel?.seller

            b.status = transactionModel?.status

            b.createdDateText = transactionModel?.createdDate?.toFormattedDateString()
            b.expiryDateText = transactionModel?.expiresDate?.toFormattedDateString()

            if (transactionModel?.status == TransactionStatus.Pending && accountModel != null) {
                val isInitiator = transactionModel?.createdById == accountModel!!.id
                b.showInitiatorActionButtons = isInitiator
                b.showRecipientActionButtons = !isInitiator
            } else {
                b.showInitiatorActionButtons = false
                b.showRecipientActionButtons = false
            }
        }
    }

    private fun updateChartItemsView() {
        val chartItems = traceabilityCounts?.let {
            listOf(
                PieChartItem("Full", it.full, ColorSuccess),
                PieChartItem("Partial", it.partial, ColorMulberryPurple),
                PieChartItem("Conditional", it.conditional, ColorSeaBlue),
                PieChartItem("Incomplete", it.incomplete, ColorMidnightBlue),
            )
        } ?: emptyList()

        updateBinding { b ->
            b.chartItems = chartItems.filter { it.value > 0 }

            b.downloadEnabled = transactionModel?.traceability != null && transactionModel?.status == TransactionStatus.Accepted
            b.dialogDescription = resourceProvider.getString(
                R.string.downloading_transaction_details,
                chartItems.sumOf { it.value })
        }
    }

    private fun addGeoDataClicked() {
        transactionModel?.let {
            setEffect(TransactionDetailsContract.Effect.NavigateAddGeolocation(it))
        }
    }

    private fun acceptTransaction() {
        launch {
            setEffect(TransactionDetailsContract.Effect.ToggleAcceptLoader(true))

            interactor.updateTransactionStatus(transactionId, TransactionStatus.Accepted.statusName)
                .onSuccess {
                    setEffect(
                        TransactionDetailsContract.Effect.ToggleAcceptLoader(false),
                        TransactionDetailsContract.Effect.StatusChanged
                    )
                    statusUpdated = true
                    refresh()
                }
                .onError {
                    val errorMessage = errorHandler.parseError(it)

                    setEffect(
                        TransactionDetailsContract.Effect.ToggleAcceptLoader(false),
                        TransactionDetailsContract.Effect.ShowMessage(errorMessage)
                    )
                }
        }
    }

    private fun rejectTransaction() {
        launch {
            setEffect(TransactionDetailsContract.Effect.ToggleRejectLoader(true))

            interactor.updateTransactionStatus(transactionId, TransactionStatus.Rejected.statusName)
                .onSuccess {
                    setEffect(
                        TransactionDetailsContract.Effect.ToggleRejectLoader(false),
                        TransactionDetailsContract.Effect.StatusChanged
                    )
                    refresh()
                }
                .onError {
                    val errorMessage = errorHandler.parseError(it)

                    setEffect(
                        TransactionDetailsContract.Effect.ToggleRejectLoader(false),
                        TransactionDetailsContract.Effect.ShowMessage(errorMessage)
                    )
                }
        }
    }

    private fun resendNotification() {
        launch {
            setEffect(TransactionDetailsContract.Effect.ToggleResendLoader(true))

            interactor.resendNotification(transactionId)
                .onSuccess {
                    setEffect(
                        TransactionDetailsContract.Effect.ToggleResendLoader(false),
                        TransactionDetailsContract.Effect.ShowMessage(resourceProvider.getString(R.string.notification_sent))
                    )
                }
                .onError {
                    val errorMessage = errorHandler.parseError(it)

                    setEffect(
                        TransactionDetailsContract.Effect.ToggleResendLoader(false),
                        TransactionDetailsContract.Effect.ShowMessage(errorMessage)
                    )
                }
        }
    }

    private fun onInitialSuppliersHistoryClick() {
        transactionModel?.let {
            setEffect(TransactionDetailsContract.Effect.NavigateInitialSupplierHistory(it))
        }
    }
    private fun onSuppliersHistoryClick(transaction: TransactionModel) {
        transactionModel?.let {
            setEffect(TransactionDetailsContract.Effect.NavigateInitialSupplierHistory(it))
        }
        setEffect(TransactionDetailsContract.Effect.NavigateSupplierHistory(transaction))
    }
} 