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
package lr.gov.lacra.trace.presentation.createtransaction.geodata.uploadfile

import android.content.Context
import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import lr.gov.lacra.trace.base.CoreViewBinding
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.base.CoreViewSideEffect
import lr.gov.lacra.trace.domain.createtransaction.models.MFile
import lr.gov.lacra.trace.domain.transactions.models.TransactionModel

object UploadFileContract {
    data class Binding(
        var fileChosen: Boolean = false,
        var fileName: String = "",
        var fileDescription: String = "",
    ) : CoreViewBinding

    sealed class Event : CoreViewEvent {
        data class OnCreate(val file: MFile?) : Event()
        data class OnCreate2(val transactionModel: TransactionModel) : Event()
        data class OnFileChanged(val context: Context, val uri: Uri?) : Event()
        data object OnFileRemoved : Event()
        data class OnConfirm(val context: Context) : Event()
    }

    sealed class Effect : CoreViewSideEffect {
        data class FileConfirmed(val file: MFile?, val location: LatLng?): Effect()
    }
}