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
import lr.gov.lacra.trace.domain.createtransaction.models.MFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun String?.toRequestBodyOrNull(): RequestBody? {
    return this?.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun Float?.toRequestBodyOrNull(): RequestBody? {
    return this?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun Double?.toRequestBodyOrNull(): RequestBody? {
    return this?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun Boolean?.toRequestBodyOrNull(): RequestBody? {
    return this?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
}

fun prepareFilePart(
    context: Context,
    file: MFile?,
    fieldName: String = "file"
): MultipartBody.Part? {
    if (file?.uri == null) return null

    val contentResolver = context.contentResolver
    val fileName = file.name
    val inputStream = contentResolver.openInputStream(file.uri!!)!!
    val fileBytes = inputStream.readBytes()
    val requestFile = fileBytes.toRequestBody(
        contentResolver.getType(file.uri!!)?.toMediaTypeOrNull() ?: "application/octet-stream".toMediaTypeOrNull()
    )
    return MultipartBody.Part.createFormData(fieldName, fileName, requestFile)
}