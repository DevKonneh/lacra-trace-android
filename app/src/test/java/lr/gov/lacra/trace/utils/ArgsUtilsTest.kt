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
package lr.gov.lacra.trace.utils

import org.junit.Assert.*
import org.junit.Test

class ArgsUtilsTest {

    @Test
    fun `toGson converts object to JSON string`() {
        // Given
        val testData = TestData("test", 123)

        // When
        val result = testData.toGson()

        // Then
        assertNotNull(result)
        assertTrue(result!!.contains("test"))
        assertTrue(result.contains("123"))
    }

    @Test
    fun `parseGsonToObject converts JSON string to object`() {
        // Given
        val jsonString = """{"name":"test","value":123}"""

        // When
        val result: TestData? = parseGsonToObject(jsonString)

        // Then
        assertNotNull(result)
        assertEquals("test", result?.name)
        assertEquals(123, result?.value)
    }

    @Test
    fun `parseGsonToObject with null returns null`() {
        // Given
        val jsonString: String? = null

        // When
        val result: TestData? = parseGsonToObject(jsonString)

        // Then
        assertNull(result)
    }

    @Test
    fun `parseGsonToObject with invalid JSON throws exception`() {
        // Given
        val jsonString = "invalid json"

        // When & Then
        try {
            val result: TestData? = parseGsonToObject(jsonString)
            fail("Expected JsonSyntaxException to be thrown")
        } catch (e: Exception) {
            // Expected behavior - invalid JSON should throw exception
            assertNotNull(e)
        }
    }

    @Test
    fun `parseGsonToList converts JSON array to list`() {
        // Given
        val jsonString = """[{"name":"test1","value":1},{"name":"test2","value":2}]"""

        // When
        val result: List<TestData> = parseGsonToList(jsonString)

        // Then
        assertEquals(2, result.size)
        assertEquals("test1", result[0].name)
        assertEquals(1, result[0].value)
        assertEquals("test2", result[1].name)
        assertEquals(2, result[1].value)
    }

    @Test
    fun `parseGsonToList with null returns empty list`() {
        // Given
        val jsonString: String? = null

        // When
        val result: List<TestData> = parseGsonToList(jsonString)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseGsonToList with invalid JSON throws exception`() {
        // Given
        val jsonString = "invalid json"

        // When & Then
        try {
            val result: List<TestData> = parseGsonToList(jsonString)
            fail("Expected JsonSyntaxException to be thrown")
        } catch (e: Exception) {
            // Expected behavior - invalid JSON should throw exception
            assertNotNull(e)
        }
    }

    @Test
    fun `replaceSlashesForNavigation replaces forward slashes with pipes`() {
        // Given
        val input = "path/to/resource"

        // When
        val result = input.replaceSlashesForNavigation()

        // Then
        assertEquals("path|to|resource", result)
    }

    @Test
    fun `replaceSlashesForNavigation with no slashes returns same string`() {
        // Given
        val input = "path"

        // When
        val result = input.replaceSlashesForNavigation()

        // Then
        assertEquals("path", result)
    }

    @Test
    fun `returnSlashes replaces pipes with forward slashes`() {
        // Given
        val input = "path|to|resource"

        // When
        val result = input.returnSlashes()

        // Then
        assertEquals("path/to/resource", result)
    }

    @Test
    fun `returnSlashes with no pipes returns same string`() {
        // Given
        val input = "path"

        // When
        val result = input.returnSlashes()

        // Then
        assertEquals("path", result)
    }

    @Test
    fun `stringOrNull with valid string returns string`() {
        // Given
        val input = "valid string"

        // When
        val result = input.stringOrNull()

        // Then
        assertEquals("valid string", result)
    }

    @Test
    fun `stringOrNull with null returns null`() {
        // Given
        val input: String? = null

        // When
        val result = input.stringOrNull()

        // Then
        assertNull(result)
    }

    @Test
    fun `stringOrNull with empty string returns null`() {
        // Given
        val input = ""

        // When
        val result = input.stringOrNull()

        // Then
        assertNull(result)
    }

    @Test
    fun `stringOrNull with blank string returns null`() {
        // Given
        val input = "   "

        // When
        val result = input.stringOrNull()

        // Then
        assertNull(result)
    }

    @Test
    fun `stringOrNull with whitespace only returns null`() {
        // Given
        val input = "\t\n\r"

        // When
        val result = input.stringOrNull()

        // Then
        assertNull(result)
    }

    @Test
    fun `stringOrNull with trimmed empty string returns null`() {
        // Given
        val input = "  \t  "

        // When
        val result = input.stringOrNull()

        // Then
        assertNull(result)
    }

    private data class TestData(
        val name: String,
        val value: Int
    )
}