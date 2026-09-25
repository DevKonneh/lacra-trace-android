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
package com.lacra.data.settings.service

import com.lacra.data.base.response.BaseResponse
import com.lacra.data.settings.model.request.AddGadgetRequest
import com.lacra.data.settings.model.request.ChangePasswordRequest
import com.lacra.data.settings.model.request.DeleteGadgetRequest
import com.lacra.data.settings.model.request.NotificationSettingsRequest
import com.lacra.data.settings.model.response.AccountInfoResponse
import com.lacra.data.settings.model.response.NotificationSettingsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface SettingsService {
    @GET("users/profile")
    suspend fun getAccountInfo(): Response<AccountInfoResponse>

    @PATCH("users/profile/password/")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<BaseResponse>

    @DELETE("users/profile/")
    suspend fun deleteAccount(): Response<BaseResponse>

    @GET("notifications/settings/")
    suspend fun getNotificationSettings(): Response<NotificationSettingsResponse>

    @PUT("notifications/settings/")
    suspend fun updateNotificationSettings(
        @Body request: NotificationSettingsRequest
    ): Response<BaseResponse>

    @POST("users/gadgets/")
    suspend fun addGadget(
        @Body request: AddGadgetRequest
    ): Response<BaseResponse>

    @HTTP(method = "DELETE", path = "users/gadgets/", hasBody = true)
    suspend fun deleteGadget(
        @Body request: DeleteGadgetRequest
    ): Response<BaseResponse>
}