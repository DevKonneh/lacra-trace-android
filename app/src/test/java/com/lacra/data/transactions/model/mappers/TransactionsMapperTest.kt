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
package com.lacra.data.transactions.model.mappers

import com.lacra.data.commodity.model.response.Commodity
import com.lacra.data.transactions.model.response.TransactionData
import com.lacra.extensions.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class TransactionsMapperTest {

    @Test
    fun `toDomain maps updated_at to updatedDate`() {
        val updatedAt = "2025-02-03T12:30:45Z"

        val result = transactionData(updatedAt = updatedAt).toDomain()

        assertEquals(updatedAt.toLocalDateTime(), result.updatedDate)
    }

    @Test
    fun `toDomain falls back to createdDate when updated_at is missing`() {
        val createdAt = "2025-02-03T10:00:00Z"

        val result = transactionData(createdAt = createdAt, updatedAt = null).toDomain()

        assertEquals(result.createdDate, result.updatedDate)
    }

    private fun transactionData(
        createdAt: String = "2025-02-03T10:00:00Z",
        updatedAt: String? = "2025-02-03T12:30:45Z",
    ) = TransactionData(
        id = "transaction-id",
        created_at = createdAt,
        updated_at = updatedAt,
        expires_at = null,
        type = "downstream",
        status = "accepted",
        action = "buying",
        location = "gps",
        latitude = null,
        longitude = null,
        commodity = Commodity(
            id = "commodity-id",
            code = "COCOA",
            name = "Cocoa",
            unit = "kg",
            has_recipe = false,
            group = null,
            balance = null,
        ),
        volume = 10f,
        traceability = "full",
        seller = null,
        buyer = null,
        is_buying_from_farmer = false,
        is_automatic = false,
        created_by_id = "creator-id",
    )
}
