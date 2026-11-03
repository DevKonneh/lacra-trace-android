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
package lr.gov.lacra.trace.data.notifications.model.mappers

import lr.gov.lacra.trace.data.base.common.toDomain
import lr.gov.lacra.trace.data.notifications.model.entity.NotificationEntity
import lr.gov.lacra.trace.data.notifications.model.response.NotificationData
import lr.gov.lacra.trace.data.notifications.model.response.NotificationsResponse
import lr.gov.lacra.trace.data.notifications.model.response.ShortTransactionData
import lr.gov.lacra.trace.data.transactions.model.mappers.toDomain
import lr.gov.lacra.trace.domain.common.PaginationModel
import lr.gov.lacra.trace.domain.notifications.models.NotificationModel
import lr.gov.lacra.trace.domain.notifications.models.NotificationStatus
import lr.gov.lacra.trace.domain.notifications.models.NotificationType
import lr.gov.lacra.trace.domain.transactions.models.TransactionModel
import lr.gov.lacra.trace.extensions.toLocalDateTime
import lr.gov.lacra.trace.utils.gson

fun NotificationsResponse.toDomain(): Pair<PaginationModel, List<NotificationModel>> {
    return pagination.toDomain() to data.map { it.toDomain() }
}

fun NotificationData.toDomain(): NotificationModel {
    return NotificationModel(
        id = id,
        createdDate = created_at.toLocalDateTime(),
        transactionId = data?.transaction_id,
        transaction = data?.toDomain(),
        type = NotificationType.entries.find { it.typeName == type },
        status = NotificationStatus.entries.find { it.statusName == status },
        receivedBy = received_by?.toDomain(),
        createdBy = created_by?.toDomain(),
    )
}

fun ShortTransactionData.toDomain(): TransactionModel? {
    return transaction?.toDomain()
}

fun NotificationModel.toEntity(): NotificationEntity {
    return NotificationEntity(
        id = id,
        type = type?.typeName,
        dataJson = gson.toJson(this),
    )
}

fun NotificationEntity.toDomain(): NotificationModel {
    return gson.fromJson(dataJson, NotificationModel::class.java)
}