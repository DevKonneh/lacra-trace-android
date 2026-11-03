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
package lr.gov.lacra.trace.data.commodity.repository

import lr.gov.lacra.trace.data.base.common.BaseResult
import lr.gov.lacra.trace.data.commodity.model.mappers.toDomain
import lr.gov.lacra.trace.data.commodity.model.request.ConvertCommodityRequest
import lr.gov.lacra.trace.data.commodity.service.ConvertCommodityService
import lr.gov.lacra.trace.data.transactions.model.mappers.toDomain
import lr.gov.lacra.trace.domain.commodity.models.ConvertRecipeModel
import lr.gov.lacra.trace.domain.transactions.models.BaseModel
import lr.gov.lacra.trace.network.handleResponse
import lr.gov.lacra.trace.network.mapResult

interface ConvertCommodityRepository {
    suspend fun getRecipes(
        search: String?,
        commodityId: String?,
        page: Int,
        pageSize: Int,
    ): BaseResult<List<ConvertRecipeModel>>

    suspend fun convertCommodity(
        request: ConvertCommodityRequest,
    ): BaseResult<BaseModel>
}

class ConvertCommodityRepositoryImpl(
    private val service: ConvertCommodityService,
) : ConvertCommodityRepository {

    override suspend fun getRecipes(
        search: String?,
        commodityId: String?,
        page: Int,
        pageSize: Int,
    ): BaseResult<List<ConvertRecipeModel>> {
        return handleResponse {
            service.getRecipes(
                search = search,
                commodityId = commodityId,
                page = page,
                pageSize = pageSize,
            )
        }.mapResult { it?.toDomain() }
    }

    override suspend fun convertCommodity(
        request: ConvertCommodityRequest,
    ): BaseResult<BaseModel> {
        return handleResponse {
            service.convertCommodity(request)
        }.mapResult { it?.toDomain() }
    }
}