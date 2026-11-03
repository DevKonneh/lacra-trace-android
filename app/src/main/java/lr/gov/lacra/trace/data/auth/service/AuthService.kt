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
package lr.gov.lacra.trace.data.auth.service

import lr.gov.lacra.trace.data.auth.model.request.GoogleAuthRequest
import lr.gov.lacra.trace.data.auth.model.request.LoginRequest
import lr.gov.lacra.trace.data.auth.model.request.OtpRequest
import lr.gov.lacra.trace.data.auth.model.request.RegistrationRequest
import lr.gov.lacra.trace.data.auth.model.response.LoginResponse
import lr.gov.lacra.trace.data.auth.model.response.RegistrationResponse
import lr.gov.lacra.trace.data.base.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("auth/jwt/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/registration/")
    suspend fun register(@Body request: RegistrationRequest): Response<RegistrationResponse>

    @POST("auth/otp/password-reset/send/")
    suspend fun passwordResetSend(@Body request: OtpRequest): Response<BaseResponse>

    @POST("auth/otp/password-reset/check/")
    suspend fun passwordResetCheck(@Body request: OtpRequest): Response<BaseResponse>

    @POST("auth/otp/password-reset/verify/")
    suspend fun passwordResetVerify(@Body request: OtpRequest): Response<BaseResponse>

    @POST("auth/social/google/login/")
    suspend fun authGoogle(
        @Body request: GoogleAuthRequest
    ): Response<LoginResponse>
}
