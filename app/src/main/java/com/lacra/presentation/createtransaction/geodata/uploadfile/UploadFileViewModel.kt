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
package com.lacra.presentation.createtransaction.geodata.uploadfile

import android.content.Context
import android.net.Uri
import com.lacra.base.BaseViewModel
import com.lacra.base.CoreViewEvent
import com.lacra.data.geodata.model.request.UpdateGeoDataRequest
import com.lacra.domain.createtransaction.models.LocationProvider
import com.lacra.domain.createtransaction.models.MFile
import com.lacra.domain.geodata.GeoDataInteractor
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.extensions.getFileParams
import com.lacra.providers.ResourceProvider
import com.lacra.utils.GeoFileParser

class UploadFileViewModel(
    private val resourceProvider: ResourceProvider,
    private val interactor: GeoDataInteractor,
) : BaseViewModel<UploadFileContract.Binding>() {

    private var transactionId: String? = null
    private var file: MFile? = null

    override fun createBinding(): UploadFileContract.Binding {
        return UploadFileContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is UploadFileContract.Event.OnCreate -> onCreate(event.file)
            is UploadFileContract.Event.OnCreate2 -> onCreate2(event.transactionModel)
            is UploadFileContract.Event.OnFileChanged -> fileChanged(event.context, event.uri)
            is UploadFileContract.Event.OnFileRemoved -> fileRemoved()
            is UploadFileContract.Event.OnConfirm -> onConfirm(event.context)
        }
    }

    override fun copyBinding(binding: UploadFileContract.Binding): UploadFileContract.Binding {
        return binding.copy()
    }

    private fun onCreate(file: MFile?) {
        this.file = file
        updateView()
    }

    private fun onCreate2(transactionModel: TransactionModel) {
        this.transactionId = transactionModel.id
    }

    private fun fileChanged(context: Context, uri: Uri?) {
        if (uri != null) {
            file = context.getFileParams(uri)
        }

        updateView()
    }

    private fun fileRemoved() {
        file = null
        updateView()
    }

    private fun updateView() {
        updateBinding { b ->
            b.fileChosen = file != null
            b.fileName = file?.name ?: ""
            b.fileDescription = "${file?.size ?: 0}b"
        }
    }

    private fun onConfirm(context: Context) {
        launch {
            if (file == null) {
                return@launch
            }

            val locations = file?.uri?.let { GeoFileParser.parse(context, it) }

            if (transactionId != null) {
                interactor.updateTransactionGeoData(
                    transactionId = transactionId!!,
                    request = UpdateGeoDataRequest(
                        locationProvider = LocationProvider.File,
                        locationFile = file,
                    )
                )
            }

            val location = if (!locations.isNullOrEmpty()) {
                locations.first()
            } else {
                null
            }

            setEffect(UploadFileContract.Effect.FileConfirmed(file = file, location = location))
        }
    }
}
