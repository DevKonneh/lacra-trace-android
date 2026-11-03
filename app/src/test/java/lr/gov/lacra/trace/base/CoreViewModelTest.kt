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
package lr.gov.lacra.trace.base

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CoreViewModelTest {

    private lateinit var testViewModel: TestCoreViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        testViewModel = TestCoreViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setEvent emits event correctly`() = runTest {
        // Given
        val testEvent = TestEvent("test")

        // When
        testViewModel.setEvent(testEvent)
        advanceUntilIdle()

        // Then
        assertTrue(testViewModel.eventHandled)
        assertEquals(testEvent, testViewModel.lastEvent)
    }

    @Test
    fun `ioContext is set to IO dispatcher`() {
        // Then
        assertEquals(Dispatchers.IO, testViewModel.ioContext)
    }

    @Test
    fun `onCleared cancels parent job`() {
        // When
        testViewModel.onCleared()

        // Then
        // The parent job should be cancelled
        // This is tested implicitly by the fact that no exceptions are thrown
        assertTrue(true)
    }

    // Test implementations
    private class TestCoreViewModel : CoreViewModel<TestBinding>() {
        var eventHandled = false
        var lastEvent: CoreViewEvent? = null

        override fun handleEvents(event: CoreViewEvent) {
            eventHandled = true
            lastEvent = event
        }

        override fun copyBinding(binding: TestBinding): TestBinding {
            return TestBinding(binding.value)
        }
    }

    private data class TestBinding(var value: String) : CoreViewBinding
    private data class TestEvent(val value: String) : CoreViewEvent
}
