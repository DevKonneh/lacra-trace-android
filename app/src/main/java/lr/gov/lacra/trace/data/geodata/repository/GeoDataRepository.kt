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
package lr.gov.lacra.trace.data.geodata.repository

import android.content.Context
import lr.gov.lacra.trace.data.base.common.BaseResult
import lr.gov.lacra.trace.data.geodata.model.mappers.toDomain
import lr.gov.lacra.trace.data.geodata.model.request.UpdateGeoDataRequest
import lr.gov.lacra.trace.data.geodata.service.GeoDataService
import lr.gov.lacra.trace.data.transactions.model.mappers.toDomain
import lr.gov.lacra.trace.domain.geodata.models.DownloadGeoDataModel
import lr.gov.lacra.trace.domain.transactions.models.BaseModel
import lr.gov.lacra.trace.network.handleResponse
import lr.gov.lacra.trace.network.mapResult
import lr.gov.lacra.trace.utils.prepareFilePart
import lr.gov.lacra.trace.utils.toRequestBodyOrNull
import okhttp3.ResponseBody

interface GeoDataRepository {

    suspend fun updateTransactionGeoData(
        transactionId: String,
        request: UpdateGeoDataRequest,
    ): BaseResult<Boolean>

    suspend fun requestTransactionGeoData(transactionId: String): BaseResult<BaseModel>
    suspend fun downloadTransactionGeoData(transactionId: String): BaseResult<DownloadGeoDataModel>

    suspend fun downloadTransactionGeoJson(transactionId: String): BaseResult<DownloadGeoDataModel>
    suspend fun downloadTransactionCSV(transactionId: String): BaseResult<ResponseBody>
    suspend fun downloadTransactionBundle(transactionId: String): BaseResult<ResponseBody>
}

class GeoDataRepositoryImpl(
    private val service: GeoDataService,
    private val context: Context,
) : GeoDataRepository {

    override suspend fun updateTransactionGeoData(
        transactionId: String,
        request: UpdateGeoDataRequest,
    ): BaseResult<Boolean> {
        return handleResponse {
            service.updateTransactionGeoData(
                transactionId = transactionId,
                locationProvider = request.locationProvider?.providerName.toRequestBodyOrNull(),
//                latitude = request.latitude.toRequestBodyOrNull(),
//                longitude = request.longitude.toRequestBodyOrNull(),
                file = prepareFilePart(context, request.locationFile, "location_file"),
            )
        }.mapResult { it?.success }
    }

    override suspend fun requestTransactionGeoData(transactionId: String): BaseResult<BaseModel> {
        return handleResponse {
            service.requestTransactionGeoData(transactionId = transactionId)
        }.mapResult { it?.toDomain() }
    }

    override suspend fun downloadTransactionGeoData(transactionId: String): BaseResult<DownloadGeoDataModel> {
        return handleResponse {
            service.downloadTransactionGeoData(transactionId = transactionId)
        }.mapResult { it?.toDomain() }
    }

    override suspend fun downloadTransactionGeoJson(transactionId: String): BaseResult<DownloadGeoDataModel> {
        return handleResponse {
            service.downloadTransactionGeoData(transactionId = transactionId)
        }.mapResult { it?.toDomain() }
    }

    override suspend fun downloadTransactionCSV(transactionId: String): BaseResult<ResponseBody> {
        return handleResponse {
            service.downloadTransactionCSV(transactionId = transactionId)
        }
    }

    override suspend fun downloadTransactionBundle(transactionId: String): BaseResult<ResponseBody> {
        return handleResponse {
            service.downloadTransactionBundle(transactionId = transactionId)
        }
    }
}