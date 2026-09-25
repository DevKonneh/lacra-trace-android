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
package com.lacra.domain.notifications.models

import android.os.Parcelable
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.domain.transactions.models.UserModel
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class NotificationModel(
    val id: String,
    val createdDate: LocalDateTime,
    val transactionId: String?,
    val transaction: TransactionModel?,
    val type: NotificationType?,
    val status: NotificationStatus?,
    val receivedBy: UserModel?,
    val createdBy: UserModel?,
) : Parcelable

enum class NotificationType(val typeName: String) {
    TransactionPending("transaction_pending"),
    TransactionAccepted("transaction_accepted"),
    TransactionRejected("transaction_rejected"),
    TransactionExpired("transaction_expired"),
    GeoDataMissing("geodata_missing"),
    GeoDataUpdated("geodata_updated"),
}

enum class NotificationStatus(val statusName: String) {
    Pending("pending"),
    Read("read"),
}

data class NotificationsFilter(
    var query: String? = null,
    var types: List<NotificationType>? = null,
    var status: NotificationStatus? = null,
    var dateStart: LocalDateTime? = null,
    var dateEnd: LocalDateTime? = null,
    var user: UserModel? = null,
)

fun NotificationsFilter.allFieldsNull(): Boolean {
    return query == null &&
            types == null &&
            status == null &&
            dateStart == null &&
            dateEnd == null &&
            user == null
}