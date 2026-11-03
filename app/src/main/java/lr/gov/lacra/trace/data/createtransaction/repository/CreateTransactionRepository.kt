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
package lr.gov.lacra.trace.data.createtransaction.repository

import android.content.Context
import lr.gov.lacra.trace.data.base.common.BaseResult
import lr.gov.lacra.trace.data.createtransaction.model.entity.PendingTransactionEntity
import lr.gov.lacra.trace.data.createtransaction.model.request.CreateDownstreamTransactionRequest
import lr.gov.lacra.trace.data.createtransaction.model.request.CreateProducerTransactionRequest
import lr.gov.lacra.trace.data.createtransaction.model.response.toDomain
import lr.gov.lacra.trace.data.createtransaction.service.CreateTransactionService
import lr.gov.lacra.trace.data.createtransaction.service.PendingTransactionsDao
import lr.gov.lacra.trace.domain.transactions.models.TransactionModel
import lr.gov.lacra.trace.network.handleResponse
import lr.gov.lacra.trace.network.mapResult
import lr.gov.lacra.trace.utils.prepareFilePart
import lr.gov.lacra.trace.utils.toRequestBodyOrNull

interface CreateTransactionRepository {
    suspend fun createProducerTransaction(request: CreateProducerTransactionRequest): BaseResult<TransactionModel>

    suspend fun createDownstreamTransaction(request: CreateDownstreamTransactionRequest): BaseResult<TransactionModel>

    suspend fun getPendingTransactions(): List<PendingTransactionEntity>

    suspend fun addPendingTransaction(transaction: PendingTransactionEntity)

    suspend fun removePendingTransaction(transaction: PendingTransactionEntity)

    suspend fun removePendingTransaction(transactionId: Long)

    suspend fun clearPendingTransactions()
}

class CreateTransactionRepositoryImpl(
    private val service: CreateTransactionService,
    private val dao: PendingTransactionsDao,
    private val context: Context,
) : CreateTransactionRepository {

    override suspend fun createProducerTransaction(
        request: CreateProducerTransactionRequest,
    ): BaseResult<TransactionModel> {
        return handleResponse {
            service.createProducerTransaction(
                commodityId = request.commodityId.toRequestBodyOrNull(),
                volume = request.volume.toRequestBodyOrNull(),
                locationProvider = request.locationProvider.toRequestBodyOrNull(),
                farmLatitude = request.farmLatitude.toRequestBodyOrNull(),
                farmLongitude = request.farmLongitude.toRequestBodyOrNull(),
                file = prepareFilePart(context, request.locationFile, "location_file"),
                transactionLatitude = request.transactionLatitude.toRequestBodyOrNull(),
                transactionLongitude = request.transactionLongitude.toRequestBodyOrNull(),
                isBuyingFromFarmer = request.isBuyingFromFarmer.toRequestBodyOrNull(),
                recipient = request.recipient.toRequestBodyOrNull(),
                harvestSeasonId = request.harvestSeasonId.toRequestBodyOrNull(),
            )
        }.mapResult { it?.toDomain() }
    }

    override suspend fun createDownstreamTransaction(
        request: CreateDownstreamTransactionRequest,
    ): BaseResult<TransactionModel> {
        return handleResponse {
            service.createDownstreamTransaction(
                commodityId = request.commodityId.toRequestBodyOrNull(),
                volume = request.volume.toRequestBodyOrNull(),
                transactionLatitude = request.transactionLatitude.toRequestBodyOrNull(),
                transactionLongitude = request.transactionLongitude.toRequestBodyOrNull(),
                action = request.action.toRequestBodyOrNull(),
                recipient = request.recipient.toRequestBodyOrNull(),
                harvestSeasonId = request.harvestSeasonId.toRequestBodyOrNull(),
            )
        }.mapResult { it?.toDomain() }
    }

    override suspend fun getPendingTransactions(): List<PendingTransactionEntity> {
        return dao.getAll()
    }

    override suspend fun addPendingTransaction(transaction: PendingTransactionEntity) {
        dao.insert(transaction)
    }

    override suspend fun removePendingTransaction(transaction: PendingTransactionEntity) {
        dao.delete(transaction)
    }

    override suspend fun removePendingTransaction(transactionId: Long) {
        dao.delete(transactionId)
    }

    override suspend fun clearPendingTransactions() {
        dao.clearAll()
    }
}
