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
package lr.gov.lacra.trace.data.base.common

import lr.gov.lacra.trace.domain.common.PaginationModel
import org.junit.Assert.*
import org.junit.Test

class PaginationTest {

    @Test
    fun `toDomain converts Pagination to PaginationModel correctly`() {
        // Given
        val pagination = Pagination(
            count = 100,
            page = 1,
            page_size = 10,
            next_page = 2,
            previous_page = null,
            total_pages = 10
        )

        // When
        val result = pagination.toDomain()

        // Then
        assertEquals(100, result.count)
        assertEquals(1, result.page)
        assertEquals(10, result.pageSize)
        assertEquals(2, result.nextPage)
        assertNull(result.previousPage)
        assertEquals(10, result.totalPages)
    }

    @Test
    fun `toDomain handles null next_page correctly`() {
        // Given
        val pagination = Pagination(
            count = 50,
            page = 5,
            page_size = 10,
            next_page = null,
            previous_page = 4,
            total_pages = 5
        )

        // When
        val result = pagination.toDomain()

        // Then
        assertEquals(50, result.count)
        assertEquals(5, result.page)
        assertEquals(10, result.pageSize)
        assertNull(result.nextPage)
        assertEquals(4, result.previousPage)
        assertEquals(5, result.totalPages)
    }

    @Test
    fun `toDomain handles null previous_page correctly`() {
        // Given
        val pagination = Pagination(
            count = 25,
            page = 1,
            page_size = 25,
            next_page = 2,
            previous_page = null,
            total_pages = 3
        )

        // When
        val result = pagination.toDomain()

        // Then
        assertEquals(25, result.count)
        assertEquals(1, result.page)
        assertEquals(25, result.pageSize)
        assertEquals(2, result.nextPage)
        assertNull(result.previousPage)
        assertEquals(3, result.totalPages)
    }

    @Test
    fun `toDomain handles zero values correctly`() {
        // Given
        val pagination = Pagination(
            count = 0,
            page = 0,
            page_size = 0,
            next_page = null,
            previous_page = null,
            total_pages = 0
        )

        // When
        val result = pagination.toDomain()

        // Then
        assertEquals(0, result.count)
        assertEquals(0, result.page)
        assertEquals(0, result.pageSize)
        assertNull(result.nextPage)
        assertNull(result.previousPage)
        assertEquals(0, result.totalPages)
    }

    @Test
    fun `toDomain handles large values correctly`() {
        // Given
        val pagination = Pagination(
            count = 1000000,
            page = 1000,
            page_size = 1000,
            next_page = 1001,
            previous_page = 999,
            total_pages = 1000
        )

        // When
        val result = pagination.toDomain()

        // Then
        assertEquals(1000000, result.count)
        assertEquals(1000, result.page)
        assertEquals(1000, result.pageSize)
        assertEquals(1001, result.nextPage)
        assertEquals(999, result.previousPage)
        assertEquals(1000, result.totalPages)
    }

    @Test
    fun `toDomain handles single page correctly`() {
        // Given
        val pagination = Pagination(
            count = 5,
            page = 1,
            page_size = 10,
            next_page = null,
            previous_page = null,
            total_pages = 1
        )

        // When
        val result = pagination.toDomain()

        // Then
        assertEquals(5, result.count)
        assertEquals(1, result.page)
        assertEquals(10, result.pageSize)
        assertNull(result.nextPage)
        assertNull(result.previousPage)
        assertEquals(1, result.totalPages)
    }
}
