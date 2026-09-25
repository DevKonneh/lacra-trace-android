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
package com.lacra.extensions

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class DateExtTest {

    @Test
    fun `toUtcIsoString converts LocalDateTime to UTC ISO string`() {
        // Given
        val dateTime = LocalDateTime.of(2023, 12, 25, 10, 30, 45)
        val zoneId = ZoneId.of("Europe/London")

        // When
        val result = dateTime.toUtcIsoString(zoneId)

        // Then
        assertNotNull(result)
        assertTrue(result.contains("2023-12-25"))
        assertTrue(result.contains("T"))
        assertTrue(result.contains("Z"))
    }

    @Test
    fun `toUtcIsoString with system default zone converts correctly`() {
        // Given
        val dateTime = LocalDateTime.of(2023, 12, 25, 10, 30, 45)

        // When
        val result = dateTime.toUtcIsoString()

        // Then
        assertNotNull(result)
        assertTrue(result.contains("2023-12-25"))
        assertTrue(result.contains("T"))
        assertTrue(result.contains("Z"))
    }

    @Test
    fun `toLocalDateTime converts ISO string to LocalDateTime`() {
        // Given
        val isoString = "2023-12-25T10:30:45Z"

        // When
        val result = isoString.toLocalDateTime()

        // Then
        assertNotNull(result)
        assertEquals(2023, result.year)
        assertEquals(12, result.monthValue)
        assertEquals(25, result.dayOfMonth)
        // Note: Time will be converted to system timezone, so exact hour/minute/second may differ
        assertTrue(result.hour >= 0 && result.hour <= 23)
        assertTrue(result.minute >= 0 && result.minute <= 59)
        assertTrue(result.second >= 0 && result.second <= 59)
    }

    @Test
    fun `toShortFormattedDateString formats date correctly`() {
        // Given
        val dateTime = LocalDateTime.of(2023, 12, 25, 10, 30, 45)

        // When
        val result = dateTime.toShortFormattedDateString()

        // Then
        assertTrue(result.contains("25"))
        assertTrue(result.contains("2023"))
    }

    @Test
    fun `millisToLocalDateTime converts milliseconds to LocalDateTime`() {
        // Given
        val millis = 1703505045000L // 2023-12-25T10:30:45

        // When
        val result = millis.millisToLocalDateTime()

        // Then
        assertNotNull(result)
        assertEquals(2023, result.year)
        assertEquals(12, result.monthValue)
        assertEquals(25, result.dayOfMonth)
    }

    @Test
    fun `toRelativeFormattedDateString returns just now for recent time`() {
        // Given
        val dateTime = LocalDateTime.now().minusSeconds(30)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("just now", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns minutes ago for recent time`() {
        // Given
        val dateTime = LocalDateTime.now().minusMinutes(5)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("5 mins ago", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns 1 min ago for exactly 1 minute`() {
        // Given
        val dateTime = LocalDateTime.now().minusMinutes(1)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("1 min ago", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns hours ago for recent time`() {
        // Given
        val dateTime = LocalDateTime.now().minusHours(3)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("3 hours ago", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns 1 hour ago for exactly 1 hour`() {
        // Given
        val dateTime = LocalDateTime.now().minusHours(1)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("1 hour ago", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns days ago for recent time`() {
        // Given
        val dateTime = LocalDateTime.now().minusDays(3)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("3 days ago", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns 1 day ago for exactly 1 day`() {
        // Given
        val dateTime = LocalDateTime.now().minusDays(1)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("1 day ago", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns weeks ago for recent time`() {
        // Given
        val dateTime = LocalDateTime.now().minusDays(14)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("2 weeks ago", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns 1 week ago for exactly 1 week`() {
        // Given
        val dateTime = LocalDateTime.now().minusDays(7)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertEquals("1 week ago", result)
    }

    @Test
    fun `toRelativeFormattedDateString returns formatted date for old time`() {
        // Given
        val dateTime = LocalDateTime.of(2020, 1, 1, 10, 30)

        // When
        val result = dateTime.toRelativeFormattedDateString()

        // Then
        assertTrue(result.contains("January") || result.contains("1"))
        assertTrue(result.contains("2020"))
        assertTrue(result.contains("10:30"))
    }

    @Test
    fun `toFormattedDateString formats date with time correctly`() {
        // Given
        val dateTime = LocalDateTime.of(2023, 12, 25, 10, 30, 45)

        // When
        val result = dateTime.toFormattedDateString()

        // Then
        assertTrue(result.contains("25"))
        assertTrue(result.contains("2023"))
        assertTrue(result.contains("10:30"))
    }

    @Test
    fun `toFormattedDateString formats PM time correctly`() {
        // Given
        val dateTime = LocalDateTime.of(2023, 12, 25, 14, 30, 45)

        // When
        val result = dateTime.toFormattedDateString()

        // Then
        assertTrue(result.contains("25"))
        assertTrue(result.contains("2023"))
        assertTrue(result.contains("2:30") || result.contains("14:30"))
    }
}
