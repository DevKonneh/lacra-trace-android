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

import com.lacra.R
import com.lacra.base.CoreViewBinding
import com.lacra.base.CoreViewEvent
import com.lacra.base.CoreViewSideEffect
import com.lacra.domain.notifications.models.NotificationStatus
import com.lacra.domain.notifications.models.NotificationType
import com.lacra.domain.notifications.models.NotificationsState
import com.lacra.domain.transactions.models.TransactionModel
import java.time.LocalDateTime

enum class NotificationsTab(val tabNameRes: Int) {
    All(tabNameRes = R.string.all_notifications),
    Pending(tabNameRes = R.string.require_actions)
}

object NotificationsContract {
    data class Binding(
        var currentTab: NotificationsTab = NotificationsTab.entries.first(),
        var tabStates: MutableMap<NotificationsTab, NotificationsState> = NotificationsTab.entries.associateWith { NotificationsState.Empty }.toMutableMap(),

        var query: String? = null,
        var dateStart: LocalDateTime? = null,
        var dateEnd: LocalDateTime? = null,
        var types: List<NotificationType>? = null,
        var status: NotificationStatus? = null,
    ) : CoreViewBinding

    sealed class Event : CoreViewEvent {
        data object OnCreate : Event()

        data class TabChanged(val tab: NotificationsTab) : Event()
        data class Refresh(val tab: NotificationsTab) : Event()
        data class NextPage(val tab: NotificationsTab) : Event()

        data class QueryChanged(val query: String) : Event()
        data class DatesChanged(val dateStart: LocalDateTime? = null, val dateEnd: LocalDateTime? = null) : Event()
        data class TypesChanged(val types: List<NotificationType>) : Event()
        data class StatusChanged(val status: NotificationStatus) : Event()

        data class DetailsClicked(val transaction: TransactionModel) : Event()
    }

    sealed class Effect : CoreViewSideEffect {
        data class NavigateTransactionDetails(val transaction: TransactionModel): Effect()
    }
}