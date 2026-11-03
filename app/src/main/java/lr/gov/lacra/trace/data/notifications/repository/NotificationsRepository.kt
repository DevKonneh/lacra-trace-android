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
package lr.gov.lacra.trace.data.notifications.repository

import lr.gov.lacra.trace.data.base.common.BaseResult
import lr.gov.lacra.trace.data.notifications.model.mappers.toDomain
import lr.gov.lacra.trace.data.notifications.model.mappers.toEntity
import lr.gov.lacra.trace.data.notifications.model.request.NotificationStatusRequest
import lr.gov.lacra.trace.data.notifications.service.NotificationsDao
import lr.gov.lacra.trace.data.notifications.service.NotificationsService
import lr.gov.lacra.trace.domain.common.PaginationModel
import lr.gov.lacra.trace.domain.notifications.models.NotificationModel
import lr.gov.lacra.trace.network.handleResponse
import lr.gov.lacra.trace.network.mapResult

interface NotificationsRepository {
    suspend fun getNotifications(
        search: String?,
        page: Int?,
        pageSize: Int?,
        types: List<String>?,
        status: String?,
        createdAtFrom: String?,
        createdAtTo: String?,
        userId: String?,
    ): BaseResult<Pair<PaginationModel, List<NotificationModel>>>

    suspend fun getNotificationsFromDB(types: List<String>?): List<NotificationModel>
    suspend fun updateNotificationsDB(items: List<NotificationModel>?)

    suspend fun updateNotificationStatus(
        notificationId: String,
        request: NotificationStatusRequest,
    ): BaseResult<Boolean>
}

class NotificationsRepositoryImpl(
    private val service: NotificationsService,
    private val dao: NotificationsDao,
) : NotificationsRepository {

    override suspend fun getNotifications(
        search: String?,
        page: Int?,
        pageSize: Int?,
        types: List<String>?,
        status: String?,
        createdAtFrom: String?,
        createdAtTo: String?,
        userId: String?,
    ): BaseResult<Pair<PaginationModel, List<NotificationModel>>> {
        return handleResponse {
            service.getNotifications(
                search = search,
                page = page,
                pageSize = pageSize,
                types = types,
                status = status,
                createdAtFrom = createdAtFrom,
                createdAtTo = createdAtTo,
                userId = userId,
            )
        }.mapResult { it?.toDomain() }
    }

    override suspend fun getNotificationsFromDB(types: List<String>?): List<NotificationModel> {
        return if (types.isNullOrEmpty()) {
            dao.getAll().map { it.toDomain() }
        } else {
            dao.getAll(types).map { it.toDomain() }
        }
    }

    override suspend fun updateNotificationsDB(items: List<NotificationModel>?) {
        dao.clearAll()
        if (items != null) {
            dao.insertAll(items.map { it.toEntity() })
        }
    }

    override suspend fun updateNotificationStatus(
        notificationId: String,
        request: NotificationStatusRequest,
    ): BaseResult<Boolean> {
        return handleResponse {
            service.updateNotificationStatus(
                notificationId = notificationId,
                request = request
            )
        }.mapResult { it?.success }
    }
}