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
package lr.gov.lacra.trace.data

import androidx.room.Database
import androidx.room.RoomDatabase
import lr.gov.lacra.trace.data.commodity.model.entity.CommodityBalanceEntity
import lr.gov.lacra.trace.data.commodity.model.entity.CommodityGroupEntity
import lr.gov.lacra.trace.data.commodity.service.CommodityBalancesDao
import lr.gov.lacra.trace.data.commodity.service.CommodityGroupsDao
import lr.gov.lacra.trace.data.createtransaction.model.entity.PendingTransactionEntity
import lr.gov.lacra.trace.data.createtransaction.service.PendingTransactionsDao
import lr.gov.lacra.trace.data.harvestseasons.model.entity.HarvestSeasonEntity
import lr.gov.lacra.trace.data.harvestseasons.service.HarvestSeasonsDao
import lr.gov.lacra.trace.data.notifications.model.entity.NotificationEntity
import lr.gov.lacra.trace.data.notifications.service.NotificationsDao
import lr.gov.lacra.trace.data.settings.model.entity.NotificationSettingEntity
import lr.gov.lacra.trace.data.settings.service.NotificationSettingsDao
import lr.gov.lacra.trace.data.transactions.model.entity.TraceabilityCountsEntity
import lr.gov.lacra.trace.data.transactions.model.entity.TransactionEntity
import lr.gov.lacra.trace.data.transactions.service.TraceabilityCountsDao
import lr.gov.lacra.trace.data.transactions.service.TransactionsDao

@Database(
    entities = [
        CommodityGroupEntity::class,
        TransactionEntity::class,
        PendingTransactionEntity::class,
        NotificationSettingEntity::class,
        NotificationEntity::class,
        TraceabilityCountsEntity::class,
        HarvestSeasonEntity::class,
        CommodityBalanceEntity::class,
    ],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commodityGroupsDao(): CommodityGroupsDao
    abstract fun commodityBalancesDao(): CommodityBalancesDao
    abstract fun transactionsDao(): TransactionsDao
    abstract fun pendingTransactionsDao(): PendingTransactionsDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
    abstract fun notificationsDao(): NotificationsDao
    abstract fun traceabilityCountsDao(): TraceabilityCountsDao
    abstract fun harvestSeasonsDao(): HarvestSeasonsDao
}
