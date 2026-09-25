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
package com.lacra.presentation.transactions.transactiondetails

import android.content.Context
import android.net.Uri
import com.lacra.base.CoreViewBinding
import com.lacra.base.CoreViewEvent
import com.lacra.base.CoreViewSideEffect
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.domain.transactions.models.TransactionsState

object SuppliersHistoryContract {
    data class Binding(
        var title: String = "",
        var state: TransactionsState = TransactionsState.Empty,
        var transactions: List<TransactionModel> = emptyList(),
        var showInitialActionButtons: Boolean = false,
        var showActionButtons: Boolean = false,
        var dialogDescription: String = "",
    ) : CoreViewBinding

    sealed class Event : CoreViewEvent {
        data class OnCreate(val transactionModel: TransactionModel, val isInitial: Boolean = true) : Event()

        data object Refresh : Event()
        data object NextPage : Event()
        data class OnSupplierClick(val transaction: TransactionModel) : Event()
        data object DownloadGeoJson : Event()
        data object DownloadCSV : Event()
        data object DownloadBundle : Event()
        data class SaveToFile(val context: Context, val uri: Uri) : Event()
        data object RequestLocations : Event()
        data object OnBackToInitialClick : Event()
    }

    sealed class Effect : CoreViewSideEffect {
        data class ShowMessage(val message: String) : Effect()
        data class ToggleDataLoader(val isLoading: Boolean) : Effect()
        data class ToggleRequestLoader(val isLoading: Boolean) : Effect()
        data class TextDataDownloaded(val filename: String) : Effect()
        data class ZipDataDownloaded(val filename: String) : Effect()
        data class CSVDataDownloaded(val filename: String) : Effect()
        data class NavigateSupplier(val transaction: TransactionModel) : Effect()
        data object NavigateInitialSupplier : Effect()
    }
}