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
package com.lacra.domain.transactions

import android.content.Context
import com.lacra.data.base.common.BaseResult
import com.lacra.data.transactions.repository.TransactionsRepository
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.domain.transactions.models.TransactionsFilter
import com.lacra.domain.transactions.models.TransactionsState
import com.lacra.domain.transactions.models.allFieldsNull
import com.lacra.extensions.isNetworkAvailable
import com.lacra.extensions.toUtcIsoString
import com.lacra.network.ErrorHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface TransactionsInteractor {
    val stateFlow: SharedFlow<TransactionsState>

    suspend fun refresh(filter: TransactionsFilter, useCache: Boolean = true)
    suspend fun loadNextPage(filter: TransactionsFilter)
}

class TransactionsInteractorImpl(
    private val repository: TransactionsRepository,
    private val context: Context,
    private val errorHandler: ErrorHandler,
) : TransactionsInteractor {

    private val transactions = mutableListOf<TransactionModel>()
    private var page = FIRST_PAGE

    private var state = MutableStateFlow<TransactionsState>(TransactionsState.Empty)

    override val stateFlow: StateFlow<TransactionsState>
        get() = state.asStateFlow()

    override suspend fun refresh(filter: TransactionsFilter, useCache: Boolean) {

        if (useCache) {
            val dbTransactions = repository.getTransactionsFromDB(
                search = filter.query,
                status = filter.status?.statusName,
                action = filter.action?.actionName,
                createdAtFrom = filter.dateStart,
                createdAtTo = filter.dateEnd,
                commodityGroupId = filter.commodityGroup?.id,
                buyerId = filter.user?.id,
            )

            transactions.clear()
            transactions.addAll(dbTransactions)
        }

        state.emit(TransactionsState.Reloading)
        delay(300)

        if (context.isNetworkAvailable()) {
            transactions.clear()
            state.emit(loadPage(FIRST_PAGE, filter))

        } else {
            if (transactions.isEmpty()) {
                state.emit(TransactionsState.Empty)
            } else {
                state.emit(TransactionsState.End(transactions))
            }
        }
    }

    override suspend fun loadNextPage(filter: TransactionsFilter) {
        state.emit(TransactionsState.PageLoading(transactions))
        state.emit(loadPage(page + 1, filter))
    }

    private suspend fun loadPage(page: Int, filter: TransactionsFilter): TransactionsState {
        val result = repository.getTransactions(
            search = filter.query,
            page = page,
            pageSize = DEFAULT_PAGE_SIZE,
            status = filter.status?.statusName,
            action = filter.action?.actionName,
            createdAtFrom = filter.dateStart?.toUtcIsoString(),
            createdAtTo = filter.dateEnd?.toUtcIsoString(),
            commodityGroupId = filter.commodityGroup?.id,
            buyerId = filter.user?.id,
        )

        if (result is BaseResult.Success && result.data != null) {
            transactions.addAll(result.data.second)

            if (filter.allFieldsNull()) {
                repository.updateTransactionsDB(result.data.second)
            }

            this.page = page

            return if (page == result.data.first.totalPages) {
                if (transactions.isEmpty()) {
                    TransactionsState.Empty
                } else {
                    TransactionsState.End(transactions)
                }
            } else {
                TransactionsState.Success(transactions)
            }
        }

        if (result is BaseResult.Error) {
            val errorMessage = errorHandler.parseError(result.exception)
            return TransactionsState.Error(errorMessage)
        }

        return TransactionsState.Error("unexpected")
    }

    companion object {
        private const val FIRST_PAGE = 1
        private const val DEFAULT_PAGE_SIZE = 20
    }
}