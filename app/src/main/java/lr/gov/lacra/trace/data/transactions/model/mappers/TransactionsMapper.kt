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
package lr.gov.lacra.trace.data.transactions.model.mappers

import com.google.android.gms.maps.model.LatLng
import lr.gov.lacra.trace.data.base.common.toDomain
import lr.gov.lacra.trace.data.commodity.model.mappers.toDomain
import lr.gov.lacra.trace.data.harvestseasons.model.mappers.toDomainOrNull
import lr.gov.lacra.trace.data.transactions.model.entity.TransactionEntity
import lr.gov.lacra.trace.data.transactions.model.response.BaseResponse
import lr.gov.lacra.trace.data.transactions.model.response.TransactionData
import lr.gov.lacra.trace.data.transactions.model.response.TransactionDetailsResponse
import lr.gov.lacra.trace.data.transactions.model.response.TransactionsResponse
import lr.gov.lacra.trace.data.transactions.model.response.User
import lr.gov.lacra.trace.domain.common.PaginationModel
import lr.gov.lacra.trace.domain.createtransaction.models.LocationProvider
import lr.gov.lacra.trace.domain.transactions.models.BaseModel
import lr.gov.lacra.trace.domain.transactions.models.GadgetTypes
import lr.gov.lacra.trace.domain.transactions.models.TraceabilityStatus
import lr.gov.lacra.trace.domain.transactions.models.TransactionAction
import lr.gov.lacra.trace.domain.transactions.models.TransactionModel
import lr.gov.lacra.trace.domain.transactions.models.TransactionStatus
import lr.gov.lacra.trace.domain.transactions.models.TransactionType
import lr.gov.lacra.trace.domain.transactions.models.UserModel
import lr.gov.lacra.trace.extensions.toLocalDateTime
import lr.gov.lacra.trace.utils.gson

fun BaseResponse.toDomain(): BaseModel {
    return BaseModel(
        success = success,
        message = message,
    )
}

fun TransactionsResponse.toDomain(): Pair<PaginationModel, List<TransactionModel>> {
    return pagination.toDomain() to data.map { it.toDomain() }
}

fun TransactionDetailsResponse.toDomain(): TransactionModel {
    return data.toDomain()
}

fun TransactionData.toDomain(): TransactionModel {
    val type = TransactionType.entries.find { it.typeName == type }
    val createdDate = created_at.toLocalDateTime()

    val status = if (is_automatic) {
        TransactionStatus.Automatic
    } else if (type == TransactionType.Producer) {
        TransactionStatus.Recorded
    } else {
        TransactionStatus.entries.find { it.statusName == status }
    }

    return TransactionModel(
        id = id,
        createdDate = createdDate,
        updatedDate = updated_at?.toLocalDateTime() ?: createdDate,
        expiresDate = expires_at?.toLocalDateTime(),
        type = type,
        status = status,
        action = TransactionAction.entries.find { it.actionName == action },
        locationProvider = LocationProvider.entries.find { it.providerName == location },
        location = getTransactionLocation(),
        commodity = commodity.toDomain(),
        volume = volume,
        traceability = TraceabilityStatus.entries.find { it.statusName == traceability },
        seller = seller?.toDomain(),
        buyer = buyer?.toDomain(),
        isBuyingFromFarmer = is_buying_from_farmer,
        isAutomatic = is_automatic,
        harvestSeason = harvest_season?.toDomainOrNull(),
        createdById = created_by_id,
    )
}

private fun TransactionData.getTransactionLocation(): LatLng? {
    val latitudeValue = transaction_latitude ?: latitude ?: farm_latitude
    val longitudeValue = transaction_longitude ?: longitude ?: farm_longitude

    return if (latitudeValue != null && longitudeValue != null) {
        LatLng(latitudeValue, longitudeValue)
    } else {
        null
    }
}

fun User.toDomain(): UserModel {
    var email: String? = null
    var phone: String? = null

    gadgets?.forEach {
        if (it.is_verified) {
            when (it.type) {
                GadgetTypes.Email.gadgetName -> email = it.identifier
                GadgetTypes.Phone.gadgetName -> phone = it.identifier
            }
        }
    }

    return UserModel(
        id = id,
        username = username,
        email = email,
        phone = phone,
    )
}

fun TransactionModel.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        status = status?.statusName,
        action = action?.actionName,
        dataJson = gson.toJson(this),
    )
}

fun TransactionEntity.toDomain(): TransactionModel {
    return gson.fromJson(dataJson, TransactionModel::class.java)
}
