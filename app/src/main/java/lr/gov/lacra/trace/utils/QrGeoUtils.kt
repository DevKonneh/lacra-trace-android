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

import android.content.Context
import androidx.core.net.toUri
import com.google.android.gms.maps.model.LatLng
import lr.gov.lacra.trace.domain.createtransaction.models.MFile
import lr.gov.lacra.trace.utils.geojson.GeoJsonCoordinate
import lr.gov.lacra.trace.utils.geojson.GeoJsonParser
import java.io.File

data class QrGeoData(
    val location: LatLng? = null,
    val geoJson: String? = null
)

fun parseQrGeoData(qr: String): QrGeoData {
    val geoJson = GeoJsonParser.parse(qr) ?: return QrGeoData()
    val location = geoJson.firstCoordinate?.toLatLng()

    return QrGeoData(
        location = location,
        geoJson = geoJson.geoJson
    )
}

private fun GeoJsonCoordinate.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun writeQRDataToCacheFile(
    context: Context,
    content: String
): Pair<File?, MFile?> {
    try {
        val fileName = "qr.geojson"
        val file = File(context.cacheDir, fileName)
        file.writeText(content)

        return file to MFile(file.toUri(), fileName, file.length())
    } catch (e: Exception) {
        e.printStackTrace()
        return null to null
    }
}
