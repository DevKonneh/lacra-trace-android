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
package lr.gov.lacra.trace.data.harvestseasons.repository

import lr.gov.lacra.trace.data.base.common.BaseResult
import lr.gov.lacra.trace.data.harvestseasons.model.entity.HarvestSeasonEntity
import lr.gov.lacra.trace.data.harvestseasons.model.mappers.getCommodityIds
import lr.gov.lacra.trace.data.harvestseasons.model.mappers.toDomain
import lr.gov.lacra.trace.data.harvestseasons.model.mappers.toEntity
import lr.gov.lacra.trace.data.harvestseasons.service.HarvestSeasonsDao
import lr.gov.lacra.trace.data.harvestseasons.service.HarvestSeasonsService
import lr.gov.lacra.trace.domain.common.PaginationModel
import lr.gov.lacra.trace.domain.harvestseasons.models.HarvestSeasonModel
import lr.gov.lacra.trace.domain.harvestseasons.models.HarvestSeasonStatus
import lr.gov.lacra.trace.network.handleResponse
import lr.gov.lacra.trace.network.mapResult
import java.time.LocalDate

interface HarvestSeasonsRepository {
    suspend fun getHarvestSeasons(
        commodityIds: List<String>?,
        status: String?,
        page: Int,
        pageSize: Int,
    ): BaseResult<Pair<PaginationModel, List<HarvestSeasonModel>>>

    suspend fun getHarvestSeasonsFromDB(
        commodityIds: List<String>?,
        status: String?,
        allowUnlinkedFallback: Boolean = true,
    ): List<HarvestSeasonModel>

    suspend fun updateHarvestSeasonsDB(
        items: List<HarvestSeasonModel>?,
        commodityIds: List<String>?,
        replaceAll: Boolean,
    )
}

class HarvestSeasonsRepositoryImpl(
    private val service: HarvestSeasonsService,
    private val dao: HarvestSeasonsDao,
    private val harvestSeasonCommodityLinker: HarvestSeasonCommodityLinker,
) : HarvestSeasonsRepository {

    override suspend fun getHarvestSeasons(
        commodityIds: List<String>?,
        status: String?,
        page: Int,
        pageSize: Int,
    ): BaseResult<Pair<PaginationModel, List<HarvestSeasonModel>>> {
        val result = handleResponse {
            service.getHarvestSeasons(
                commodityIds = commodityIds,
                status = status,
                page = page,
                pageSize = pageSize,
            )
        }.mapResult { it?.toDomain() }

        if (result is BaseResult.Success) {
            updateHarvestSeasonsDB(
                items = result.data?.second,
                commodityIds = commodityIds,
                replaceAll = commodityIds == null && status == null,
            )
            harvestSeasonCommodityLinker.linkStoredHarvestSeasonsToBalances()
        }

        return result
    }

    override suspend fun getHarvestSeasonsFromDB(
        commodityIds: List<String>?,
        status: String?,
        allowUnlinkedFallback: Boolean,
    ): List<HarvestSeasonModel> {
        val requestedCommodityIds = commodityIds.orEmpty().toSet()
        val statusFilteredEntities = dao.getAll()
            .filter { entity -> status == null || entity.status == status }
        val commodityFilteredEntities = if (requestedCommodityIds.isEmpty()) {
            statusFilteredEntities
        } else {
            val linkedEntities = statusFilteredEntities.filter { entity ->
                entity.getCommodityIds().any { it in requestedCommodityIds }
            }

            linkedEntities.takeIf { it.isNotEmpty() }
                ?: statusFilteredEntities.takeIf { allowUnlinkedFallback }?.filter { entity ->
                    entity.getCommodityIds().isEmpty()
                }.orEmpty()
        }

        return commodityFilteredEntities
            .map { it.toDomain() }
            .sortedHarvestSeasons()
    }

    override suspend fun updateHarvestSeasonsDB(
        items: List<HarvestSeasonModel>?,
        commodityIds: List<String>?,
        replaceAll: Boolean,
    ) {
        val seasons = items ?: return
        val existing = dao.getAll().associateBy { it.id }
        val requestCommodityIds = commodityIds.orEmpty()
        val entities = seasons.map { season ->
            val existingCommodityIds = getExistingCommodityIds(existing[season.id])
            val mergedCommodityIds = (existingCommodityIds + requestCommodityIds).distinct()
            season.toEntity(mergedCommodityIds)
        }

        if (replaceAll) {
            dao.clearAll()
        }

        dao.insertAll(entities)
    }
}

private fun List<HarvestSeasonModel>.sortedHarvestSeasons(): List<HarvestSeasonModel> {
    return distinctBy { it.id }
        .sortedWith(
            compareBy<HarvestSeasonModel> { it.status.sortPriority() }
                .thenByDescending { it.startDate ?: LocalDate.MIN }
                .thenBy { it.name }
        )
}

private fun HarvestSeasonStatus?.sortPriority(): Int {
    return when (this) {
        HarvestSeasonStatus.Active -> 0
        HarvestSeasonStatus.Past -> 1
        HarvestSeasonStatus.Archived -> 2
        null -> Int.MAX_VALUE
    }
}

private fun getExistingCommodityIds(entity: HarvestSeasonEntity?): List<String> {
    return entity?.getCommodityIds().orEmpty()
}
