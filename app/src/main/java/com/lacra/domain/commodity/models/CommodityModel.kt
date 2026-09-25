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
package com.lacra.domain.commodity.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommodityModel(
    val id: String,
    val code: String,
    val name: String,
    val unit: String,
    val hasRecipe: Boolean,
    val group: CommodityGroupModel?,
    val balance: Float?,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return other is CommodityModel && id == other.id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + code.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + unit.hashCode()
        return result
    }

    fun getBalanceText(): String {
        return "${balance ?: 0f} $unit"
    }
}

@Parcelize
data class CommodityGroupModel(
    val id: String,
    val name: String,
    val commodities: List<CommodityModel>?,
) : Parcelable

data class CommodityFilter(
    var query: String? = null,
)

data class CommodityBalanceFilter(
    var query: String? = null,
    var groupId: String? = null,
    var commodityId: String? = null,
)