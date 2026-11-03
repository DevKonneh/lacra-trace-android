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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface CoreViewBinding
interface CoreViewEvent
interface CoreViewSideEffect

abstract class CoreViewModel<T : CoreViewBinding> : ViewModel() {
    open val ioContext = Dispatchers.IO
    private val mainContext = Dispatchers.Main

    private val parentJob: Job
        get() = SupervisorJob()

    private val scope: CoroutineScope
        get() = CoroutineScope(Dispatchers.Main + parentJob)

    private var toggle = false
    private lateinit var binding: T
    private val viewBinding = MutableLiveData<Pair<Boolean, T>>()

    private val _event = MutableSharedFlow<CoreViewEvent>()
    val event: SharedFlow<CoreViewEvent>
        get() = _event

    private val _effect = MutableSharedFlow<CoreViewSideEffect>()
    val effect: SharedFlow<CoreViewSideEffect>
        get() = _effect

    init {
        subscribeToEvents()
    }

    public override fun onCleared() {
        parentJob.cancel()
        super.onCleared()
    }

    fun setEvent(event: CoreViewEvent) {
        scope.launch {
            _event.emit(event)
        }
    }

    open fun handleEvents(event: CoreViewEvent) {
        // Override in child classes
    }

    protected fun setEffect(vararg effects: CoreViewSideEffect) {
        scope.launch {
            effects.forEach { _effect.emit(it) }
        }
    }

    private fun subscribeToEvents() {
        scope.launch {
            event.collect {
                handleEvents(it)
            }
        }
    }

    protected fun launch(
        dispatcher: CoroutineContext = mainContext,
        scope: CoroutineScope = viewModelScope,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return scope.launch(dispatcher) { this.block() }
    }

    fun initBinding(b: T) {
        binding = b
    }

    @Composable
    fun observeViewBinding(): T? {
        return viewBinding.observeAsState().value?.second
    }

    fun updateBinding(action: (T) -> Unit) {
        action.invoke(binding)
        toggle = !toggle
        viewBinding.postValue(Pair(toggle, copyBinding(binding)))
    }

    abstract fun copyBinding(binding: T): T
} 