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
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.google.android.gms.maps.model.LatLng
import com.opencsv.CSVReader
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

interface GeoParserStrategy {
    fun parse(context: Context, uri: Uri): List<LatLng>
}

object GeoFileParser {
    fun parse(context: Context, uri: Uri): List<LatLng> {
        val extension = getFileExtension(context, uri) ?: return emptyList()
        val strategy = getStrategy(extension.lowercase()) ?: return emptyList()
        return strategy.parse(context, uri)
    }

    private fun getStrategy(ext: String): GeoParserStrategy? {
        return when (ext) {
            "json", "geojson" -> GeoJsonParser()
            "geoid" -> GeoIdParser()
            "csv" -> CsvParser()
            "gpx" -> GpxParser()
            "kmz" -> KmzParser()
            "gpkg" -> GpkgParser()
            else -> null
        }
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        return DocumentFile.fromSingleUri(context, uri)
            ?.name
            ?.substringAfterLast('.', "")
            ?.lowercase()
            ?.takeIf { it.isNotEmpty() }
    }
}

private class GeoJsonParser : GeoParserStrategy {
    override fun parse(context: Context, uri: Uri): List<LatLng> {
        val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
            ?: return emptyList()
        return Regex("""\[\s*(-?\d+(?:\.\d+)?),\s*(-?\d+(?:\.\d+)?)\s*]""")
            .findAll(json)
            .mapNotNull {
                val lon = it.groupValues[1].toDoubleOrNull()
                val lat = it.groupValues[2].toDoubleOrNull()
                if (lat != null && lon != null) LatLng(lat, lon) else null
            }.toList()
    }
}

private class GeoIdParser : GeoParserStrategy {
    override fun parse(context: Context, uri: Uri): List<LatLng> {
        return context.contentResolver.openInputStream(uri)?.bufferedReader()?.useLines { lines ->
            lines.mapNotNull {
                val (lat, lon) = it.split(",").mapNotNull(String::toDoubleOrNull)
                if (lat != null && lon != null) LatLng(lat, lon) else null
            }.toList()
        } ?: emptyList()
    }
}

private class CsvParser : GeoParserStrategy {
    override fun parse(context: Context, uri: Uri): List<LatLng> {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return emptyList()
        val reader = CSVReader(InputStreamReader(inputStream))
        val rows = reader.readAll()

        if (rows.isEmpty()) return emptyList()

        val header = rows[0].map { it.trim().lowercase() }
        val latIndex = header.indexOfFirst { it.contains("lat", ignoreCase = true) }
        val lonIndex = header.indexOfFirst { it.contains("lon", ignoreCase = true) }

        if (latIndex == -1 || lonIndex == -1) return emptyList()

        return rows.drop(1).mapNotNull { row ->
            val lat = row.getOrNull(latIndex)?.toDoubleOrNull()
            val lon = row.getOrNull(lonIndex)?.toDoubleOrNull()
            if (lat != null && lon != null) LatLng(lat, lon) else null
        }
    }
}

private class GpxParser : GeoParserStrategy {
    override fun parse(context: Context, uri: Uri): List<LatLng> {
        val result = mutableListOf<LatLng>()
        val input = context.contentResolver.openInputStream(uri) ?: return emptyList()
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(input, null)

        var event = parser.eventType
        while (event != org.xmlpull.v1.XmlPullParser.END_DOCUMENT) {
            if (event == org.xmlpull.v1.XmlPullParser.START_TAG && parser.name == "trkpt") {
                val lat = parser.getAttributeValue(null, "lat")?.toDoubleOrNull()
                val lon = parser.getAttributeValue(null, "lon")?.toDoubleOrNull()
                if (lat != null && lon != null) result.add(LatLng(lat, lon))
            }
            event = parser.next()
        }
        return result
    }
}

private class KmzParser : GeoParserStrategy {
    override fun parse(context: Context, uri: Uri): List<LatLng> {
        val result = mutableListOf<LatLng>()
        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    if (entry.name.endsWith(".kml")) {
                        val text = zip.bufferedReader().readText()
                        Regex("<coordinates>(.*?)</coordinates>")
                            .findAll(text)
                            .flatMap { match ->
                                match.groupValues[1].split("\\s+".toRegex()).mapNotNull { coord ->
                                    val parts = coord.split(",")
                                    val lon = parts.getOrNull(0)?.toDoubleOrNull()
                                    val lat = parts.getOrNull(1)?.toDoubleOrNull()
                                    if (lat != null && lon != null) LatLng(lat, lon) else null
                                }
                            }.forEach { result.add(it) }
                    }
                    entry = zip.nextEntry
                }
            }
        }
        return result
    }
}

private class GpkgParser : GeoParserStrategy {
    override fun parse(context: Context, uri: Uri): List<LatLng> {
        val file = File(context.cacheDir, "temp.gpkg")
        context.contentResolver.openInputStream(uri)?.use { it.copyTo(file.outputStream()) }

        val db = android.database.sqlite.SQLiteDatabase.openDatabase(
            file.path, null, android.database.sqlite.SQLiteDatabase.OPEN_READONLY
        )

        val result = mutableListOf<LatLng>()
        try {
            val cursor = db.rawQuery(
                "SELECT ST_AsText(geometry) FROM (SELECT geometry FROM sqlite_master UNION ALL SELECT geometry FROM features)",
                null
            )
            while (cursor.moveToNext()) {
                val wkt = cursor.getString(0)
                Regex("POINT\\(([-\\d.]+) ([-\\d.]+)\\)").find(wkt)?.let {
                    val lon = it.groupValues[1].toDoubleOrNull()
                    val lat = it.groupValues[2].toDoubleOrNull()
                    if (lat != null && lon != null) result.add(LatLng(lat, lon))
                }
            }
            cursor.close()
        } catch (_: Exception) {
        } finally {
            db.close()
            file.delete()
        }

        return result
    }
}

private class ExcelParser : GeoParserStrategy {
    override fun parse(context: Context, uri: Uri): List<LatLng> {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return emptyList()
        return parseExcel(inputStream)
    }

    private fun parseExcel(inputStream: InputStream): List<LatLng> {
        val result = mutableListOf<LatLng>()
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0) ?: return emptyList()

        val headerRow = sheet.getRow(0) ?: return emptyList()
        val latIndex = headerRow.indexOfFirst { it?.stringCellValue?.contains("lat", ignoreCase = true) == true }
        val lonIndex = headerRow.indexOfFirst { it?.stringCellValue?.contains("lon", ignoreCase = true) == true }

        if (latIndex == -1 || lonIndex == -1) return emptyList()

        for (i in 1..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue
            val lat = row.getCell(latIndex)?.numericCellValue
            val lon = row.getCell(lonIndex)?.numericCellValue
            if (lat != null && lon != null) result.add(LatLng(lat, lon))
        }

        workbook.close()
        return result
    }

    private fun org.apache.poi.ss.usermodel.Row.indexOfFirst(predicate: (org.apache.poi.ss.usermodel.Cell?) -> Boolean): Int {
        for (i in 0 until this.lastCellNum) {
            if (predicate(this.getCell(i))) return i
        }
        return -1
    }
}