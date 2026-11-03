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
package lr.gov.lacra.trace.presentation.createtransaction.geodata.qr

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.data.base.common.BaseResult
import lr.gov.lacra.trace.data.geodata.model.request.UpdateGeoDataRequest
import lr.gov.lacra.trace.domain.geodata.GeoDataInteractor
import lr.gov.lacra.trace.domain.geodata.models.DownloadGeoDataModel
import lr.gov.lacra.trace.domain.transactions.models.BaseModel
import lr.gov.lacra.trace.network.ErrorHandler
import lr.gov.lacra.trace.providers.ResourceProvider
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class QrScanViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var resourceProvider: TestResourceProvider
    private lateinit var interactor: TestGeoDataInteractor
    private lateinit var viewModel: QrScanViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        resourceProvider = TestResourceProvider()
        interactor = TestGeoDataInteractor()
        viewModel = QrScanViewModel(
            resourceProvider = resourceProvider,
            interactor = interactor,
            errorHandler = ErrorHandler(resourceProvider),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `non GeoJSON QR emits message and does not upload`() = runTest {
        val message = async {
            viewModel.effect
                .filterIsInstance<QrScanContract.Effect.ShowMessage>()
                .first()
        }

        viewModel.setEvent(QrScanContract.Event.QrScanned(mockk<Context>(), "not geojson"))
        advanceUntilIdle()

        assertEquals(resourceProvider.invalidQrMessage, message.await().message)
        assertFalse(interactor.updateTransactionGeoDataCalled)
    }

    private class TestResourceProvider : ResourceProvider {
        val invalidQrMessage = "QR code must contain GeoJSON data"

        override fun resources(): Resources {
            throw NotImplementedError()
        }

        override fun getString(res: Int, vararg args: Any): String {
            return when (res) {
                R.string.invalid_qr_geojson -> invalidQrMessage
                else -> "string-$res"
            }
        }

        override fun getColor(color: Int): Int {
            throw NotImplementedError()
        }

        override fun getDrawable(icon: Int): Drawable? {
            throw NotImplementedError()
        }

        override fun cacheDir(): File {
            throw NotImplementedError()
        }
    }

    private class TestGeoDataInteractor : GeoDataInteractor {
        var updateTransactionGeoDataCalled = false

        override suspend fun updateTransactionGeoData(
            transactionId: String,
            request: UpdateGeoDataRequest
        ): BaseResult<Boolean> {
            updateTransactionGeoDataCalled = true
            return BaseResult.Success(true)
        }

        override suspend fun requestTransactionGeoData(transactionId: String): BaseResult<BaseModel> {
            throw NotImplementedError()
        }

        override suspend fun downloadTransactionGeoData(transactionId: String): BaseResult<DownloadGeoDataModel> {
            throw NotImplementedError()
        }

        override suspend fun downloadTransactionGeoJson(transactionId: String): BaseResult<DownloadGeoDataModel> {
            throw NotImplementedError()
        }

        override suspend fun downloadTransactionCSV(transactionId: String): BaseResult<ResponseBody> {
            throw NotImplementedError()
        }

        override suspend fun downloadTransactionBundle(transactionId: String): BaseResult<ResponseBody> {
            throw NotImplementedError()
        }
    }
}
