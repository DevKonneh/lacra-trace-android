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
package com.lacra.data.settings.repository

import com.lacra.data.base.common.BaseResult
import com.lacra.data.settings.model.mappers.toDomain
import com.lacra.data.settings.model.mappers.toEntity
import com.lacra.data.settings.model.request.AddGadgetRequest
import com.lacra.data.settings.model.request.ChangePasswordRequest
import com.lacra.data.settings.model.request.DeleteGadgetRequest
import com.lacra.data.settings.model.request.NotificationSettingsRequest
import com.lacra.data.settings.service.NotificationSettingsDao
import com.lacra.data.settings.service.SettingsService
import com.lacra.domain.settings.models.AccountModel
import com.lacra.domain.settings.models.NotificationSettingsModel
import com.lacra.network.handleResponse
import com.lacra.network.mapResult

interface SettingsRepository {
    suspend fun getAccountInfo(): BaseResult<AccountModel>
    suspend fun changePassword(request: ChangePasswordRequest): BaseResult<String>
    suspend fun deleteAccount(): BaseResult<String>
    suspend fun getNotificationSettings(): BaseResult<List<NotificationSettingsModel>>
    suspend fun getNotificationSettingsFromDB(): List<NotificationSettingsModel>
    suspend fun updateNotificationSettings(request: NotificationSettingsRequest): BaseResult<String>
    suspend fun updateNotificationSettingsDB(items: List<NotificationSettingsModel>?)
    suspend fun addGadget(request: AddGadgetRequest): BaseResult<Boolean>
    suspend fun deleteGadget(request: DeleteGadgetRequest): BaseResult<Boolean>
}

class SettingsRepositoryImpl(
    private val service: SettingsService,
    private val dao: NotificationSettingsDao,
) : SettingsRepository {

    override suspend fun getAccountInfo(): BaseResult<AccountModel> {
        return handleResponse {
            service.getAccountInfo()
        }.mapResult { it?.toDomain() }
    }

    override suspend fun changePassword(request: ChangePasswordRequest): BaseResult<String> {
        return handleResponse {
            service.changePassword(request)
        }.mapResult { it?.message ?: "Password changed" }
    }

    override suspend fun deleteAccount(): BaseResult<String> {
        return handleResponse {
            service.deleteAccount()
        }.mapResult { it?.message ?: "Profile deleted" }
    }

    override suspend fun getNotificationSettings(): BaseResult<List<NotificationSettingsModel>> {
        return handleResponse {
            service.getNotificationSettings()
        }.mapResult { it?.toDomain() }
    }

    override suspend fun getNotificationSettingsFromDB(): List<NotificationSettingsModel> {
        return dao.getAll().map { it.toDomain() }
    }

    override suspend fun updateNotificationSettings(request: NotificationSettingsRequest): BaseResult<String> {
        return handleResponse {
            service.updateNotificationSettings(request)
        }.mapResult { it?.message }
    }

    override suspend fun updateNotificationSettingsDB(items: List<NotificationSettingsModel>?) {
        dao.clearAll()
        if (items != null) {
            dao.insertAll(items.map { it.toEntity() })
        }
    }

    override suspend fun addGadget(request: AddGadgetRequest): BaseResult<Boolean> {
        return handleResponse {
            service.addGadget(request)
        }.mapResult { it?.success }
    }

    override suspend fun deleteGadget(request: DeleteGadgetRequest): BaseResult<Boolean> {
        return handleResponse {
            service.deleteGadget(request)
        }.mapResult { it?.success }
    }
}