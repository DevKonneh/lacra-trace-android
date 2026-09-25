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
package com.lacra.utils.geojson

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class GeoJsonParserTest {

    @Test
    fun `parse reads GeoJSON polygon using longitude latitude order`() {
        val geoJson = """{"type":"FeatureCollection","generatedBy":"Ground","features":[{"type":"Feature","properties":{"farmer":"Amina","plot":{"id":"plot-1"}},"geometry":{"type":"Polygon","coordinates":[[[13.405,52.52,34.0],[13.406,52.52],[13.406,52.521],[13.405,52.52]]]}}]}"""

        val result = GeoJsonParser.parse(geoJson)

        assertEquals(geoJson, result?.geoJson)
        assertCoordinate(result?.firstCoordinate, latitude = 52.52, longitude = 13.405)
    }

    @Test
    fun `parse accepts GeoJSON with root and nested extra fields`() {
        val geoJson = """{"type":"Feature","id":"ground-feature-1","extraRootField":{"source":"FAO"},"geometry":{"type":"Point","coordinates":[-1.2921,36.8219]},"properties":{"name":"Farm","extraNestedField":{"value":42}}}"""

        val result = GeoJsonParser.parse(geoJson)

        assertEquals(geoJson, result?.geoJson)
        assertCoordinate(result?.firstCoordinate, latitude = 36.8219, longitude = -1.2921)
    }

    @Test
    fun `parse extracts GeoJSON object from wrapper payload`() {
        val geoJson = """{"type":"Feature","geometry":{"type":"Point","coordinates":[11.25,4.75]},"properties":{"name":"Farm"}}"""
        val wrapper = """{"cardId":"ground-card-1","geojson":$geoJson,"version":1}"""

        val result = GeoJsonParser.parse(wrapper)

        assertEquals(geoJson, result?.geoJson)
        assertCoordinate(result?.firstCoordinate, latitude = 4.75, longitude = 11.25)
    }

    @Test
    fun `parse returns null for malformed GeoJSON shape`() {
        val result = GeoJsonParser.parse("""{"type":"FeatureCollection","features":"unexpected"}""")

        assertNull(result)
    }

    private fun assertCoordinate(
        coordinate: GeoJsonCoordinate?,
        latitude: Double,
        longitude: Double,
    ) {
        assertNotNull(coordinate)
        assertEquals(latitude, coordinate!!.latitude, 0.000001)
        assertEquals(longitude, coordinate.longitude, 0.000001)
    }
}
