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
package lr.gov.lacra.trace.domain.notifications

import android.content.Context
import lr.gov.lacra.trace.data.base.common.BaseResult
import lr.gov.lacra.trace.data.notifications.repository.NotificationsRepository
import lr.gov.lacra.trace.domain.notifications.models.NotificationModel
import lr.gov.lacra.trace.domain.notifications.models.NotificationsFilter
import lr.gov.lacra.trace.domain.notifications.models.NotificationsState
import lr.gov.lacra.trace.domain.notifications.models.allFieldsNull
import lr.gov.lacra.trace.extensions.isNetworkAvailable
import lr.gov.lacra.trace.extensions.toUtcIsoString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface NotificationsInteractor {
    val stateFlow: SharedFlow<NotificationsState>

    suspend fun refresh(filter: NotificationsFilter)
    suspend fun loadNextPage(filter: NotificationsFilter)
}

class NotificationsInteractorImpl(
    private val repository: NotificationsRepository,
    private val context: Context,
) : NotificationsInteractor {
    private val notifications = mutableListOf<NotificationModel>()
    private var page = FIRST_PAGE

    private var state = MutableStateFlow<NotificationsState>(NotificationsState.Empty)

    override val stateFlow: StateFlow<NotificationsState>
        get() = state.asStateFlow()

    override suspend fun refresh(filter: NotificationsFilter) {
        if (notifications.isEmpty()) {
            notifications.addAll(repository.getNotificationsFromDB(filter.types?.map { it.typeName }))

            if (notifications.isEmpty()) {
                state.emit(NotificationsState.Empty)
            } else {
                state.emit(NotificationsState.End(notifications))
            }
        }

        if (context.isNetworkAvailable()) {
            notifications.clear()
            state.emit(NotificationsState.Reloading)
            state.emit(loadPage(FIRST_PAGE, filter))
        }
    }

    override suspend fun loadNextPage(filter: NotificationsFilter) {
        state.emit(NotificationsState.PageLoading(notifications))
        state.emit(loadPage(page + 1, filter))
    }

    private suspend fun loadPage(page: Int, filter: NotificationsFilter): NotificationsState {
        val result = repository.getNotifications(
            search = filter.query,
            page = page,
            pageSize = DEFAULT_PAGE_SIZE,
            types = filter.types?.map { it.typeName },
            status = filter.status?.statusName,
            createdAtFrom = filter.dateStart?.toUtcIsoString(),
            createdAtTo = filter.dateEnd?.toUtcIsoString(),
            userId = filter.user?.id,
        )

        if (result is BaseResult.Success && result.data != null) {
            notifications.addAll(result.data.second)

            if (filter.allFieldsNull()) {
                repository.updateNotificationsDB(notifications)
            }

            this.page = page

            return if (page == FIRST_PAGE && notifications.isEmpty()) {
                NotificationsState.Empty

            } else if (page == result.data.first.totalPages) {
                NotificationsState.End(notifications)

            } else {
                NotificationsState.Success(notifications)
            }
        }

        if (result is BaseResult.Error) {
            return NotificationsState.Error(result.exception.message ?: "")
        }

        return NotificationsState.Error("unexpected")
    }

    companion object {
        private const val FIRST_PAGE = 1
        private const val DEFAULT_PAGE_SIZE = 20
    }
}