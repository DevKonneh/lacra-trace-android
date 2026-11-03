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
package lr.gov.lacra.trace.data.settings.model.mappers

import lr.gov.lacra.trace.data.settings.model.response.AccountData
import lr.gov.lacra.trace.data.settings.model.response.AccountInfoResponse
import lr.gov.lacra.trace.data.settings.model.response.GadgetData
import lr.gov.lacra.trace.domain.settings.models.AccountModel
import lr.gov.lacra.trace.domain.settings.models.GadgetModel
import lr.gov.lacra.trace.domain.transactions.models.GadgetTypes

fun AccountInfoResponse.toDomain(): AccountModel {
    return data.toDomain()
}

fun AccountData.toDomain(): AccountModel {
    return AccountModel(
        id = id,
        username = username,
        gadgets = gadgets.map { it.toDomain() }
    )
}

fun GadgetData.toDomain(): GadgetModel {
    return GadgetModel(
        id = id,
        type = GadgetTypes.entries.find { it.gadgetName == type },
        identifier = identifier,
        isVerified = is_verified
    )
}