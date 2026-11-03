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

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class QrGeoUtilsTest {

    @Test
    fun `parseQrGeoData reads GeoJSON polygon using longitude latitude order`() {
        val geoJson = """{"type":"FeatureCollection","generatedBy":"Ground","features":[{"type":"Feature","properties":{"farmer":"Amina","plot":{"id":"plot-1"}},"geometry":{"type":"Polygon","coordinates":[[[13.405,52.52,34.0],[13.406,52.52],[13.406,52.521],[13.405,52.52]]]}}]}"""

        val result = parseQrGeoData(geoJson)

        assertEquals(geoJson, result.geoJson)
        assertCoordinate(result.location, latitude = 52.52, longitude = 13.405)
    }

    @Test
    fun `parseQrGeoData keeps GeoJSON as upload content when root has extra fields`() {
        val geoJson = """{"type":"Feature","id":"ground-feature-1","extraRootField":{"source":"FAO"},"geometry":{"type":"Point","coordinates":[-1.2921,36.8219]},"properties":{"name":"Farm","extraNestedField":{"value":42}}}"""

        val result = parseQrGeoData(geoJson)

        assertEquals(geoJson, result.geoJson)
        assertCoordinate(result.location, latitude = 36.8219, longitude = -1.2921)
    }

    @Test
    fun `parseQrGeoData extracts GeoJSON object from surrounding QR text`() {
        val geoJson = """{"type":"Feature","geometry":{"type":"Point","coordinates":[30.5,-3.25]},"properties":{"name":"Farm"}}"""
        val qr = """
            source: FAO Ground
            $geoJson
            scanned-by: whimo
        """.trimIndent()

        val result = parseQrGeoData(qr)

        assertEquals(geoJson, result.geoJson)
        assertCoordinate(result.location, latitude = -3.25, longitude = 30.5)
    }

    @Test
    fun `parseQrGeoData extracts nested GeoJSON when QR has wrapper fields`() {
        val geoJson = """{"type":"Feature","geometry":{"type":"Point","coordinates":[11.25,4.75]},"properties":{"name":"Farm"}}"""
        val qr = """{"cardId":"ground-card-1","geojson":$geoJson,"version":1}"""

        val result = parseQrGeoData(qr)

        assertEquals(geoJson, result.geoJson)
        assertCoordinate(result.location, latitude = 4.75, longitude = 11.25)
    }

    @Test
    fun `parseQrGeoData does not crash on malformed GeoJSON`() {
        val result = parseQrGeoData("""{"type":"FeatureCollection","features":"unexpected"}""")

        assertNull(result.geoJson)
        assertNull(result.location)
    }

    @Test
    fun `parseQrGeoData ignores non GeoJSON QR content`() {
        val result = parseQrGeoData("https://maps.google.com/?query=1.25,36.5")

        assertNull(result.geoJson)
        assertNull(result.location)
    }

    private fun assertCoordinate(
        coordinate: LatLng?,
        latitude: Double,
        longitude: Double,
    ) {
        assertNotNull(coordinate)
        assertEquals(latitude, coordinate!!.latitude, 0.000001)
        assertEquals(longitude, coordinate.longitude, 0.000001)
    }
}
