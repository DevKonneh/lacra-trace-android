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
package com.lacra.domain.geodata

import com.lacra.data.base.common.BaseResult
import com.lacra.data.geodata.model.request.UpdateGeoDataRequest
import com.lacra.data.geodata.repository.GeoDataRepository
import com.lacra.domain.geodata.models.DownloadGeoDataModel
import com.lacra.domain.transactions.models.BaseModel
import okhttp3.ResponseBody

interface GeoDataInteractor {
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

class GeoDataInteractorImpl(
    private val repository: GeoDataRepository,
) : GeoDataInteractor {

    override suspend fun updateTransactionGeoData(
        transactionId: String,
        request: UpdateGeoDataRequest
    ): BaseResult<Boolean> {
        return repository.updateTransactionGeoData(transactionId, request)
    }

    override suspend fun requestTransactionGeoData(transactionId: String): BaseResult<BaseModel> {
        return repository.requestTransactionGeoData(transactionId)
    }

    override suspend fun downloadTransactionGeoData(transactionId: String): BaseResult<DownloadGeoDataModel> {
        return repository.downloadTransactionGeoData(transactionId)
    }

    override suspend fun downloadTransactionGeoJson(transactionId: String): BaseResult<DownloadGeoDataModel> {
        return repository.downloadTransactionGeoJson(transactionId)
    }

    override suspend fun downloadTransactionCSV(transactionId: String): BaseResult<ResponseBody> {
        return repository.downloadTransactionCSV(transactionId)
    }

    override suspend fun downloadTransactionBundle(transactionId: String): BaseResult<ResponseBody> {
        return repository.downloadTransactionBundle(transactionId)
    }
}