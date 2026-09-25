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
package com.lacra.data.commodity.model.mappers

import com.google.gson.reflect.TypeToken
import com.lacra.data.commodity.model.entity.CommodityGroupEntity
import com.lacra.data.commodity.model.response.CommoditiesGroupsResponse
import com.lacra.data.commodity.model.response.Commodity
import com.lacra.data.commodity.model.response.CommodityGroup
import com.lacra.domain.commodity.models.CommodityGroupModel
import com.lacra.domain.commodity.models.CommodityModel
import com.lacra.utils.gson

fun CommoditiesGroupsResponse.toDomain(): List<CommodityGroupModel> {
    return data.map { it.toDomain() }
}

fun CommodityGroup.toDomain(): CommodityGroupModel {
    return CommodityGroupModel(
        id = id,
        name = name,
        commodities = commodities?.map { it.toDomain() }
    )
}

fun Commodity.toDomain(): CommodityModel {
    return CommodityModel(
        id = id,
        code = code,
        name = name,
        unit = unit,
        hasRecipe = has_recipe,
        group = group?.toDomain(),
        balance = balance,
    )
}

fun CommodityGroupModel.toEntity(): CommodityGroupEntity {
    return CommodityGroupEntity(
        id = id,
        name = name,
        commoditiesJson = gson.toJson(commodities)
    )
}

fun CommodityGroupEntity.toDomain(): CommodityGroupModel {
    val type = object : TypeToken<List<CommodityModel>>() {}.type
    return CommodityGroupModel(
        id = id,
        name = name,
        commodities = gson.fromJson(commoditiesJson, type)
    )
}