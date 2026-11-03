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
package lr.gov.lacra.trace.presentation.balances.convert

import lr.gov.lacra.trace.base.BaseViewModel
import lr.gov.lacra.trace.base.CoreViewEvent
import lr.gov.lacra.trace.data.base.common.onError
import lr.gov.lacra.trace.data.base.common.onSuccess
import lr.gov.lacra.trace.domain.commodity.ConvertCommodityInteractor
import lr.gov.lacra.trace.domain.commodity.models.CommodityModel
import lr.gov.lacra.trace.domain.commodity.models.ConvertRecipeModel
import lr.gov.lacra.trace.network.ErrorHandler

class ConvertRecipesViewModel(
    private val interactor: ConvertCommodityInteractor,
    private val errorHandler: ErrorHandler,
) : BaseViewModel<ConvertRecipesContract.Binding>() {

    private var recipes: List<ConvertRecipeModel>? = null

    override fun createBinding(): ConvertRecipesContract.Binding {
        return ConvertRecipesContract.Binding()
    }

    override fun handleEvents(event: CoreViewEvent) {
        super.handleEvents(event)
        when (event) {
            is ConvertRecipesContract.Event.OnCreate -> onCreate(event.commodity)
        }
    }

    override fun copyBinding(binding: ConvertRecipesContract.Binding): ConvertRecipesContract.Binding {
        return binding.copy()
    }

    private fun updateView() {
        updateBinding { b ->
            b.recipes = recipes
        }
    }

    private fun onCreate(commodity: CommodityModel?) {
        if (commodity != null) {
            getRecipes(commodity)
        }
    }

    private fun getRecipes(commodity: CommodityModel) {
        launch {
            setEffect(ConvertRecipesContract.Effect.ToggleLoader(true))

            interactor.getRecipes(commodity)
                .onSuccess {
                    recipes = it
                    setEffect(ConvertRecipesContract.Effect.ToggleLoader(false))
                    updateView()
                }
                .onError {
                    it.printStackTrace()
                    val errorMessage = errorHandler.parseError(it)
                    setEffect(
                        ConvertRecipesContract.Effect.ToggleLoader(false),
                        ConvertRecipesContract.Effect.ShowMessage(errorMessage)
                    )
                }
        }
    }
}