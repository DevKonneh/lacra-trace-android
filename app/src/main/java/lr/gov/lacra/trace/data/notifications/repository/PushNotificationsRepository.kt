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
import lr.gov.lacra.trace.data.notifications.model.request.PushNotificationDeviceRequest
import lr.gov.lacra.trace.data.notifications.service.PushNotificationsService
import lr.gov.lacra.trace.network.handleResponse
import lr.gov.lacra.trace.network.mapResult

interface PushNotificationsRepository {
    suspend fun getPushNotificationDevices(): BaseResult<List<String>>
    suspend fun addPushNotificationDevice(request: PushNotificationDeviceRequest): BaseResult<Boolean>
}

class PushNotificationsRepositoryImpl(
    private val service: PushNotificationsService,
) : PushNotificationsRepository {

    override suspend fun getPushNotificationDevices(): BaseResult<List<String>> {
        return handleResponse {
            service.getPushNotificationDevices()
        }.mapResult { it?.toDomain() }
    }

    override suspend fun addPushNotificationDevice(
        request: PushNotificationDeviceRequest,
    ): BaseResult<Boolean> {
        return handleResponse {
            service.addPushNotificationDevice(request = request)
        }.mapResult { it?.success }
    }
}