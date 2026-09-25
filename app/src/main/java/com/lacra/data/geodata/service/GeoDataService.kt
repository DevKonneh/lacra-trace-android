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
package com.lacra.data.geodata.service

import com.lacra.data.geodata.model.response.DownloadGeoDataResponse
import com.lacra.data.transactions.model.response.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface GeoDataService {

    @Multipart
    @PATCH("transactions/{transaction_id}/geodata/")
    suspend fun updateTransactionGeoData(
        @Path("transaction_id") transactionId: String,
        @Part("location") locationProvider: RequestBody?,
//        @Part("latitude") latitude: RequestBody?,
//        @Part("longitude") longitude: RequestBody?,
        @Part file: MultipartBody.Part?,
    ): Response<BaseResponse>

    @POST("transactions/{transaction_id}/geodata/request/")
    suspend fun requestTransactionGeoData(
        @Path("transaction_id") transactionId: String,
    ): Response<BaseResponse>

    @GET("transactions/{transaction_id}/download/geojson/")
    suspend fun downloadTransactionGeoData(
        @Path("transaction_id") transactionId: String,
    ): Response<DownloadGeoDataResponse>

    @GET("transactions/{transaction_id}/download/geojson/")
    suspend fun downloadTransactionGeoJson(
        @Path("transaction_id") transactionId: String,
    ): Response<DownloadGeoDataResponse>

    @Streaming
    @GET("transactions/{transaction_id}/download/csv/")
    suspend fun downloadTransactionCSV(
        @Path("transaction_id") transactionId: String,
    ): Response<ResponseBody>

    @Streaming
    @GET("transactions/{transaction_id}/download/bundle/")
    suspend fun downloadTransactionBundle(
        @Path("transaction_id") transactionId: String,
    ): Response<ResponseBody>
}