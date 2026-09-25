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

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import com.lacra.domain.createtransaction.models.MFile

fun Context.getFileParams(uri: Uri): MFile {
    val mFile = MFile(uri = uri)

    val cursor = contentResolver.query(
        /* uri = */ uri,
        /* projection = */ null,
        /* selection = */ null,
        /* selectionArgs = */ null,
        /* sortOrder = */ null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

            if (nameIndex != -1) {
                mFile.name = it.getString(nameIndex)
            }
            if (sizeIndex != -1) {
                mFile.size = it.getLong(sizeIndex)
            }
        }
    }

    return mFile
}

fun Context.getContactEmail(uri: Uri): String? {
    var email: String? = null

    val cursor = contentResolver.query(
        /* uri = */ uri,
        /* projection = */ arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
        /* selection = */ null,
        /* selectionArgs = */ null,
        /* sortOrder = */ null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val emailIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

            if (emailIndex != -1)  {
                email = it.getString(emailIndex)
            }
        }
    }

    return email
}

fun Context.getContactPhone(uri: Uri): String? {
    var phone: String? = null

    val cursor = contentResolver.query(
        /* uri = */ uri,
        /* projection = */ arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
        /* selection = */ null,
        /* selectionArgs = */ null,
        /* sortOrder = */ null
    )

    cursor?.use {
        if (it.moveToFirst()) {
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            if (phoneIndex != -1)  {
                phone = it.getString(phoneIndex)
            }
        }
    }

    return phone
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetwork?.let { network ->
        connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } ?: false
}