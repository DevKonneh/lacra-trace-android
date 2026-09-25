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
package com.lacra.presentation.notifications

import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.domain.notifications.NotificationsInteractor
import com.lacra.domain.notifications.models.NotificationStatus
import com.lacra.domain.notifications.models.NotificationType
import com.lacra.domain.notifications.models.NotificationsFilter
import com.lacra.domain.notifications.models.NotificationsState
import com.lacra.domain.transactions.models.TransactionModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.time.LocalDateTime

class NotificationsViewModel(
    private val allTabInteractor: NotificationsInteractor,
    private val pendingTabInteractor: NotificationsInteractor,
) : BaseViewModel<NotificationsContract.Binding>() {

    private var searchJob: Job? = null

    private var currentTab = NotificationsTab.entries.first()
    private var tabStates: MutableMap<NotificationsTab, NotificationsState> = NotificationsTab.entries.associateWith { NotificationsState.Empty }.toMutableMap()
    private val filter = NotificationsFilter()

    override fun createBinding(): NotificationsContract.Binding {
        return NotificationsContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is NotificationsContract.Event.OnCreate -> onCreate()
            is NotificationsContract.Event.TabChanged -> tabChanged(event.tab)
            is NotificationsContract.Event.Refresh -> refresh(event.tab)
            is NotificationsContract.Event.NextPage -> nextPage(event.tab)
            is NotificationsContract.Event.QueryChanged -> queryChanged(event.query)
            is NotificationsContract.Event.DatesChanged -> datesChanged(event.dateStart, event.dateEnd)
            is NotificationsContract.Event.TypesChanged -> typesChanged(event.types)
            is NotificationsContract.Event.StatusChanged -> statusChanged(event.status)
            is NotificationsContract.Event.DetailsClicked -> detailsClicked(event.transaction)
        }
    }

    override fun copyBinding(binding: NotificationsContract.Binding): NotificationsContract.Binding {
        return binding.copy()
    }

    init {
        launch {
            allTabInteractor.stateFlow.collect { state ->
                tabStates[NotificationsTab.All] = state

                delay(100)
                updateTabs()
            }
        }
        launch {
            pendingTabInteractor.stateFlow.collect { state ->
                tabStates[NotificationsTab.Pending] = state

                delay(100)
                updateTabs()
            }
        }
    }

    private fun updateTabs() {
        updateBinding { b ->
            b.tabStates = tabStates
        }
    }

    private fun onCreate() {
        updateBinding { b ->
            b.currentTab = currentTab
            b.tabStates = tabStates

            b.query = filter.query
            b.dateStart = filter.dateStart
            b.dateEnd = filter.dateEnd

            b.types = filter.types
            b.status = filter.status
        }
    }

    private fun tabChanged(tab: NotificationsTab) {
        currentTab = tab

        if (tabStates[tab] == NotificationsState.Empty) {
            refresh(tab)
        }
    }

    private fun refresh(tab: NotificationsTab) {
        launch {
            when (tab) {
                NotificationsTab.All -> {
                    allTabInteractor.refresh(filter/*.copy(types = ALL_TAB_NOTIFICATION_TYPES)*/)
                }
                NotificationsTab.Pending -> {
                    pendingTabInteractor.refresh(filter.copy(types = PENDING_TAB_NOTIFICATION_TYPES))
                }
            }
        }
    }

    private fun nextPage(tab: NotificationsTab) {
        launch {
            when (tab) {
                NotificationsTab.All -> {
                    allTabInteractor.loadNextPage(filter/*.copy(types = ALL_TAB_NOTIFICATION_TYPES)*/)
                }
                NotificationsTab.Pending -> {
                    pendingTabInteractor.loadNextPage(filter.copy(types = PENDING_TAB_NOTIFICATION_TYPES))
                }
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

    private fun typesChanged(types: List<NotificationType>) {
        filter.types = types
        refresh(currentTab)
    }

    private fun statusChanged(status: NotificationStatus) {
        filter.status = status
        refresh(currentTab)
    }

    private fun detailsClicked(transactionModel: TransactionModel) {
        setEffect(NotificationsContract.Effect.NavigateTransactionDetails(transactionModel))
    }

    companion object {
        private val ALL_TAB_NOTIFICATION_TYPES = listOf(
            NotificationType.TransactionPending,
            NotificationType.TransactionAccepted,
            NotificationType.TransactionRejected,
            NotificationType.TransactionExpired,
            NotificationType.GeoDataMissing,
            NotificationType.GeoDataUpdated,
        )
        private val PENDING_TAB_NOTIFICATION_TYPES = listOf(
            NotificationType.TransactionPending,
            NotificationType.GeoDataMissing,
        )
    }
}