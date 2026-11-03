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
package lr.gov.lacra.trace.domain.transactions.models

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.domain.commodity.models.CommodityGroupModel
import lr.gov.lacra.trace.domain.commodity.models.CommodityModel
import lr.gov.lacra.trace.domain.createtransaction.models.LocationProvider
import lr.gov.lacra.trace.domain.harvestseasons.models.getYearRangeText
import lr.gov.lacra.trace.presentation.ui.theme.ColorError
import lr.gov.lacra.trace.presentation.ui.theme.ColorGray10
import lr.gov.lacra.trace.presentation.ui.theme.ColorGray5
import lr.gov.lacra.trace.presentation.ui.theme.ColorGray50
import lr.gov.lacra.trace.presentation.ui.theme.ColorGray90
import lr.gov.lacra.trace.presentation.ui.theme.ColorLightInfo
import lr.gov.lacra.trace.presentation.ui.theme.ColorLightGreen
import lr.gov.lacra.trace.presentation.ui.theme.ColorLightOrange
import lr.gov.lacra.trace.presentation.ui.theme.ColorLightRed
import lr.gov.lacra.trace.presentation.ui.theme.ColorSuccess
import lr.gov.lacra.trace.presentation.ui.theme.ColorWarning
import lr.gov.lacra.trace.extensions.toQuantityText
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

typealias HarvestSeasonModel = lr.gov.lacra.trace.domain.harvestseasons.models.HarvestSeasonModel
typealias HarvestSeasonStatus = lr.gov.lacra.trace.domain.harvestseasons.models.HarvestSeasonStatus

data class BaseModel(
    val success: Boolean,
    val message: String
)

@Parcelize
data class TransactionModel(
    val id: String,
    val createdDate: LocalDateTime,
    val updatedDate: LocalDateTime?,
    val expiresDate: LocalDateTime?,
    val type: TransactionType?,
    val status: TransactionStatus?,
    val action: TransactionAction?,
    val locationProvider: LocationProvider?,
    val location: LatLng?,
    val commodity: CommodityModel,
    val volume: Float,
    val traceability: TraceabilityStatus?,
    val seller: UserModel?,
    val buyer: UserModel?,
    val isBuyingFromFarmer: Boolean,
    val isAutomatic: Boolean,
    val createdById: String?,
    val harvestSeason: HarvestSeasonModel? = null,
) : Parcelable

@Parcelize
data class UserModel(
    val id: String,
    val username: String? = null,
    val email: String? = null,
    val phone: String? = null,
) : Parcelable

data class TraceabilityCountsModel(
    val full: Int,
    val partial: Int,
    val conditional: Int,
    val incomplete: Int,
)

enum class TransactionType(val typeName: String) {
    Producer("producer"),
    Downstream("downstream"),
    Conversion("conversion"),
}

enum class TransactionAction(val actionName: String) {
    Buying("buying"),
    Selling("selling"),
}

enum class TransactionStatus(val statusName: String) {
    Accepted("accepted"),
    Rejected("rejected"),
    Pending("pending"),
    NoResponse("no_response"),
    Recorded("recorded"),
    Automatic("automatic"),
}

enum class TraceabilityStatus(val statusName: String) {
    Full("full"),
    Conditional("conditional"),
    Partial("partial"),
    Incomplete("incomplete"),
}

enum class GadgetTypes(val gadgetName: String) {
    Email("email"),
    Phone("phone"),
}

data class TransactionsFilter(
    var query: String? = null,
    var status: TransactionStatus? = null,
    var action: TransactionAction? = null,
    var dateStart: LocalDateTime? = null,
    var dateEnd: LocalDateTime? = null,
    var commodityGroup: CommodityGroupModel? = null,
    var commodity: CommodityModel? = null,
    var user: UserModel? = null,
    var harvestSeason: HarvestSeasonModel? = null,
)
fun TransactionsFilter.allFieldsNull(): Boolean {
    return query == null &&
            status == null &&
            action == null &&
            dateStart == null &&
            dateEnd == null &&
            commodityGroup == null &&
            commodity == null &&
            user == null &&
            harvestSeason == null
}
fun TransactionModel.getCommodityText(): String {
    return "${commodity.code} ${commodity.name}"
}

fun TransactionModel.getCommodityVolumeText(): String {
    return "${volume.toQuantityText()} ${commodity.unit}"
}

fun TransactionModel.getCommodityFullText(): String {
    return "${getCommodityText()}, ${getCommodityVolumeText()}"
}

fun TransactionModel.getCommodityShortText(): String {
    return "${commodity.group?.name}, ${getCommodityVolumeText()}"
}

fun HarvestSeasonModel.getShortName(): String {
    if (startDate != null || endDate != null) {
        return getYearRangeText()
    }

    return name.removePrefix("Harvest season").trim().ifBlank { name }
}

fun UserModel.getAccountText(): String {
    return username ?: email ?: phone ?: ""
}
