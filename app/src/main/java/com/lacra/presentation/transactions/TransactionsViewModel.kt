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
package com.lacra.presentation.transactions

import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.domain.createtransaction.CreateTransactionInteractor
import com.lacra.domain.createtransaction.models.PendingTransactionModel
import com.lacra.domain.transactions.TransactionsInteractor
import com.lacra.domain.transactions.models.TransactionAction
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.domain.transactions.models.TransactionStatus
import com.lacra.domain.transactions.models.TransactionsFilter
import com.lacra.domain.transactions.models.TransactionsState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.time.LocalDateTime

class TransactionsViewModel(
    private val allTabInteractor: TransactionsInteractor,
    private val boughtTabInteractor: TransactionsInteractor,
    private val soldTabInteractor: TransactionsInteractor,
    private val createTransactionInteractor: CreateTransactionInteractor,
) : BaseViewModel<TransactionsContract.Binding>() {

    private var searchJob: Job? = null

    private var currentTab = TransactionsTab.entries.first()
    private var tabStates: MutableMap<TransactionsTab, TransactionsState>
    private var pendingTransactions: MutableMap<TransactionsTab, List<PendingTransactionModel>>
    private val filter = TransactionsFilter()

    override fun createBinding(): TransactionsContract.Binding {
        return TransactionsContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is TransactionsContract.Event.OnCreate -> onCreate()
            is TransactionsContract.Event.TabChanged -> tabChanged(event.tab)
            is TransactionsContract.Event.Refresh -> refresh(event.tab)
            is TransactionsContract.Event.NextPage -> nextPage(event.tab)
            is TransactionsContract.Event.QueryChanged -> queryChanged(event.query)
            is TransactionsContract.Event.DatesChanged -> datesChanged(event.dateStart, event.dateEnd)
            is TransactionsContract.Event.StatusChanged -> statusChanged(event.status)
            is TransactionsContract.Event.TransactionClicked -> transactionClicked(event.transaction)
            is TransactionsContract.Event.AddGeoDataClicked -> addGeoDataClicked(event.transaction)
            is TransactionsContract.Event.NotificationsClicked -> notificationsClicked()
        }
    }

    override fun copyBinding(binding: TransactionsContract.Binding): TransactionsContract.Binding {
        return binding.copy()
    }

    init {
        tabStates = TransactionsTab.entries
            .associateWith { TransactionsState.Empty }
            .toMutableMap()

        pendingTransactions = TransactionsTab.entries
            .associateWith { emptyList<PendingTransactionModel>() }
            .toMutableMap()

        launch {
            allTabInteractor.stateFlow.collect { state ->
                tabStates[TransactionsTab.All] = state

                delay(100)
                updateTabs()
            }
        }
        launch {
            boughtTabInteractor.stateFlow.collect { state ->
                tabStates[TransactionsTab.Bought] = state

                delay(100)
                updateTabs()
            }
        }
        launch {
            soldTabInteractor.stateFlow.collect { state ->
                tabStates[TransactionsTab.Sold] = state

                delay(100)
                updateTabs()
            }
        }
    }

    private fun updateTabs() {
        updateBinding { b ->
            b.tabStates = tabStates
            b.pendingTransactions = pendingTransactions
        }
    }

    private fun onCreate() {
        updateBinding { b ->
            b.currentTab = currentTab
            b.tabStates = tabStates
            b.pendingTransactions = pendingTransactions

            b.query = filter.query

            b.dateStart = filter.dateStart
            b.dateEnd = filter.dateEnd
            b.status = filter.status
        }
    }

    private fun tabChanged(tab: TransactionsTab) {
        currentTab = tab

        if (tabStates[tab] == TransactionsState.Empty) {
            refresh(tab)
        }
    }

    private fun refresh(tab: TransactionsTab) {
        launch {
            when (tab) {
                TransactionsTab.All -> allTabInteractor.refresh(filter)
                TransactionsTab.Bought -> boughtTabInteractor.refresh(filter.copy(action = TransactionAction.Buying))
                TransactionsTab.Sold -> soldTabInteractor.refresh(filter.copy(action = TransactionAction.Selling))
            }
        }

        showPendingTransactions()
        sendPendingTransactions()
    }

    private fun nextPage(tab: TransactionsTab) {
        launch {
            when (tab) {
                TransactionsTab.All -> allTabInteractor.loadNextPage(filter)
                TransactionsTab.Bought -> boughtTabInteractor.loadNextPage(filter.copy(action = TransactionAction.Buying))
                TransactionsTab.Sold -> soldTabInteractor.loadNextPage(filter.copy(action = TransactionAction.Selling))
            }
        }
    }

    private fun queryChanged(query: String) {
        filter.query = query
        updateBinding { b ->
            b.query = query
        }

        searchJob?.cancel()
        searchJob = launch {
            delay(600)
            refresh(currentTab)
        }
    }

    private fun datesChanged(dateStart: LocalDateTime?, dateEnd: LocalDateTime?) {
        filter.dateStart = dateStart
        filter.dateEnd = dateEnd
        refresh(currentTab)
    }

    private fun statusChanged(status: TransactionStatus) {
        filter.status = status
        refresh(currentTab)
    }

    private fun transactionClicked(transaction: TransactionModel) {
        setEffect(TransactionsContract.Effect.NavigateTransactionDetails(transaction))
    }

    private fun addGeoDataClicked(transaction: TransactionModel) {
        setEffect(TransactionsContract.Effect.NavigateAddGeolocation(transaction))
    }

    private fun notificationsClicked() {
        setEffect(TransactionsContract.Effect.NavigateNotifications)
    }

    private fun showPendingTransactions() {
        launch {
            val pendingItems = createTransactionInteractor.getPendingTransactions()

            pendingTransactions[TransactionsTab.All] = pendingItems
            pendingTransactions[TransactionsTab.Bought] = pendingItems.filter { it.transactionModel.action == TransactionAction.Buying }
            pendingTransactions[TransactionsTab.Sold] = pendingItems.filter { it.transactionModel.action == TransactionAction.Selling }

            delay(100)
            updateTabs()
        }
    }

    private fun sendPendingTransactions() {
        launch {
            if (createTransactionInteractor.sendPendingTransactions()) {
                pendingTransactions[TransactionsTab.All] = emptyList()
                pendingTransactions[TransactionsTab.Bought] = emptyList()
                pendingTransactions[TransactionsTab.Sold] = emptyList()

                delay(100)
                updateTabs()
            }
        }
    }
}