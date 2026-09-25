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
package com.lacra.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lacra.data.commodity.model.entity.CommodityGroupEntity
import com.lacra.data.commodity.service.CommodityGroupsDao
import com.lacra.data.createtransaction.model.entity.PendingTransactionEntity
import com.lacra.data.createtransaction.service.PendingTransactionsDao
import com.lacra.data.notifications.model.entity.NotificationEntity
import com.lacra.data.notifications.service.NotificationsDao
import com.lacra.data.settings.model.entity.NotificationSettingEntity
import com.lacra.data.settings.service.NotificationSettingsDao
import com.lacra.data.transactions.model.entity.TraceabilityCountsEntity
import com.lacra.data.transactions.model.entity.TransactionEntity
import com.lacra.data.transactions.service.TraceabilityCountsDao
import com.lacra.data.transactions.service.TransactionsDao

@Database(
    entities = [
        CommodityGroupEntity::class,
        TransactionEntity::class,
        PendingTransactionEntity::class,
        NotificationSettingEntity::class,
        NotificationEntity::class,
        TraceabilityCountsEntity::class,
    ],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commodityGroupsDao(): CommodityGroupsDao
    abstract fun transactionsDao(): TransactionsDao
    abstract fun pendingTransactionsDao(): PendingTransactionsDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
    abstract fun notificationsDao(): NotificationsDao
    abstract fun traceabilityCountsDao(): TraceabilityCountsDao
}