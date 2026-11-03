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
package lr.gov.lacra.trace.presentation.createtransaction.geodata

import android.content.Context
import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.domain.createtransaction.models.CreateTransactionModel
import lr.gov.lacra.trace.domain.transactions.models.TransactionModel
import lr.gov.lacra.trace.extensions.toLatLng
import lr.gov.lacra.trace.utils.checkLocationPermissionGranted
import lr.gov.lacra.trace.utils.getCurrentLocation

class FarmGeoDataViewModel : BaseViewModel<FarmGeoDataContract.Binding>() {

    private var isLocationLoading = false

    override fun createBinding(): FarmGeoDataContract.Binding {
        return FarmGeoDataContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is FarmGeoDataContract.Event.OnCreate -> onCreate(event.transaction)
            is FarmGeoDataContract.Event.OnCreate2 -> onCreate2(event.transaction)
            is FarmGeoDataContract.Event.OnRecordLocationClick -> onRecordLocationClick(event.context)
        }
    }

    override fun copyBinding(binding: FarmGeoDataContract.Binding): FarmGeoDataContract.Binding {
        return binding.copy()
    }

    private fun onCreate(transaction: CreateTransactionModel) {
        updateBinding { b ->
            b.qrButtonVisible = transaction.isBuyingFromFarmer
            b.uploadFileButtonVisible = !transaction.isBuyingFromFarmer
            b.gpsButtonVisible = transaction.isBuyingFromFarmer && transaction.isOnFarm
            b.mapButtonVisible = transaction.isBuyingFromFarmer
        }
    }

    private fun onCreate2(transaction: TransactionModel) {
        updateBinding { b ->
            b.qrButtonVisible = true
            b.uploadFileButtonVisible = true
            b.gpsButtonVisible = false
            b.mapButtonVisible = false
        }
    }

    private fun onRecordLocationClick(context: Context) {
        if (!checkLocationPermissionGranted(context)) {
            setEffect(FarmGeoDataContract.Effect.RequestLocationPermission)
            return
        }

        if (isLocationLoading) return
        isLocationLoading = true

        launch {
            setEffect(FarmGeoDataContract.Effect.ToggleLoader(true))

            try {
                val location = getCurrentLocation(context)

                setEffect(
                    if (location == null) {
                        FarmGeoDataContract.Effect.RequestLocationPermission
                    } else {
                        FarmGeoDataContract.Effect.LocationRecorded(location.toLatLng())
                    }
                )
            } finally {
                isLocationLoading = false
                setEffect(FarmGeoDataContract.Effect.ToggleLoader(false))
            }
        }
    }
}
