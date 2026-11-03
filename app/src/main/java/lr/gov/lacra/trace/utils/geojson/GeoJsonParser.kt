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
package lr.gov.lacra.trace.utils.geojson

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

data class GeoJsonParseResult(
    val geoJson: String,
    val firstCoordinate: GeoJsonCoordinate? = null
)

data class GeoJsonCoordinate(
    val latitude: Double,
    val longitude: Double
)

object GeoJsonParser {

    fun parse(text: String): GeoJsonParseResult? {
        val candidate = extractJsonObject(text) ?: return null
        val parsedObject = runCatching {
            JsonParser.parseString(candidate).asJsonObject
        }.getOrNull() ?: return null

        val geoJsonObject = findGeoJsonObject(parsedObject) ?: return null
        val geoJson = if (geoJsonObject === parsedObject) candidate.trim() else geoJsonObject.toString()

        return GeoJsonParseResult(
            geoJson = geoJson,
            firstCoordinate = findFirstGeoJsonCoordinate(geoJsonObject)
        )
    }

    private fun extractJsonObject(text: String): String? {
        val trimmed = text.trim()
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return trimmed
        }

        val startIndex = trimmed.indexOf('{')
        if (startIndex == -1) return null

        var depth = 0
        var inString = false
        var isEscaped = false

        for (index in startIndex until trimmed.length) {
            val char = trimmed[index]

            if (isEscaped) {
                isEscaped = false
                continue
            }

            when (char) {
                '\\' -> if (inString) isEscaped = true
                '"' -> inString = !inString
                '{' -> if (!inString) depth++
                '}' -> if (!inString) {
                    depth--
                    if (depth == 0) {
                        return trimmed.substring(startIndex, index + 1)
                    }
                }
            }
        }

        return null
    }

    private fun findGeoJsonObject(element: JsonElement): JsonObject? {
        if (element.isJsonObject) {
            val jsonObject = element.asJsonObject
            if (jsonObject.isSupportedGeoJsonObject()) {
                return jsonObject
            }

            jsonObject.entrySet().forEach { entry ->
                findGeoJsonObject(entry.value)?.let { return it }
            }
        } else if (element.isJsonArray) {
            element.asJsonArray.forEach { child ->
                findGeoJsonObject(child)?.let { return it }
            }
        }

        return null
    }

    private fun JsonObject.isSupportedGeoJsonObject(): Boolean {
        return when (getString("type")) {
            "FeatureCollection" -> get("features")?.isJsonArray == true
            "Feature" -> has("geometry")
            "GeometryCollection" -> get("geometries")?.isJsonArray == true
            "Point",
            "MultiPoint",
            "LineString",
            "MultiLineString",
            "Polygon",
            "MultiPolygon" -> get("coordinates")?.isJsonArray == true
            else -> false
        }
    }

    private fun findFirstGeoJsonCoordinate(jsonObject: JsonObject): GeoJsonCoordinate? {
        return when (jsonObject.getString("type")) {
            "FeatureCollection" -> {
                jsonObject.get("features")?.asJsonArrayOrNull()?.forEach { feature ->
                    feature.asJsonObjectOrNull()?.let { findFirstGeoJsonCoordinate(it) }?.let { return it }
                }
                null
            }
            "Feature" -> jsonObject.get("geometry")
                ?.takeUnless { it.isJsonNull }
                ?.asJsonObjectOrNull()
                ?.let { findFirstGeoJsonCoordinate(it) }
            "GeometryCollection" -> {
                jsonObject.get("geometries")?.asJsonArrayOrNull()?.forEach { geometry ->
                    geometry.asJsonObjectOrNull()?.let { findFirstGeoJsonCoordinate(it) }?.let { return it }
                }
                null
            }
            "Point" -> jsonObject.get("coordinates")?.asJsonArrayOrNull()?.toGeoJsonCoordinate()
            "MultiPoint",
            "LineString" -> jsonObject.get("coordinates")?.let { findFirstPosition(it, 1) }
            "MultiLineString",
            "Polygon" -> jsonObject.get("coordinates")?.let { findFirstPosition(it, 2) }
            "MultiPolygon" -> jsonObject.get("coordinates")?.let { findFirstPosition(it, 3) }
            else -> null
        }
    }

    private fun findFirstPosition(element: JsonElement, depth: Int): GeoJsonCoordinate? {
        if (depth == 0) {
            return element.asJsonArrayOrNull()?.toGeoJsonCoordinate()
        }

        element.asJsonArrayOrNull()?.forEach { child ->
            findFirstPosition(child, depth - 1)?.let { return it }
        }

        return null
    }

    private fun JsonArray.toGeoJsonCoordinate(): GeoJsonCoordinate? {
        val lon = getOrNull(0)?.asDoubleOrNull() ?: return null
        val lat = getOrNull(1)?.asDoubleOrNull() ?: return null

        if (lat.isNaN() || lat.isInfinite() || lat !in -90.0..90.0) return null
        if (lon.isNaN() || lon.isInfinite() || lon !in -180.0..180.0) return null

        return GeoJsonCoordinate(latitude = lat, longitude = lon)
    }

    private fun JsonArray.getOrNull(index: Int): JsonElement? {
        return if (index in 0 until size()) get(index) else null
    }

    private fun JsonObject.getString(key: String): String? {
        return get(key)?.takeIf { it.isJsonPrimitive }?.let {
            runCatching { it.asString }.getOrNull()
        }
    }

    private fun JsonElement.asJsonObjectOrNull(): JsonObject? {
        return takeIf { it.isJsonObject }?.asJsonObject
    }

    private fun JsonElement.asJsonArrayOrNull(): JsonArray? {
        return takeIf { it.isJsonArray }?.asJsonArray
    }

    private fun JsonElement.asDoubleOrNull(): Double? {
        return takeIf { it.isJsonPrimitive }?.let {
            runCatching { it.asDouble }.getOrNull()
        }
    }
}
