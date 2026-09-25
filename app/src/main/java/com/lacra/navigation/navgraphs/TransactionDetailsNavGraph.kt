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
package com.lacra.navigation.navgraphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.navigation.Screens
import com.lacra.presentation.transactions.transactiondetails.SuppliersHistoryScreen
import com.lacra.presentation.transactions.transactiondetails.TransactionDetailsScreen
import com.lacra.presentation.ui.components.AppNavHost
import com.lacra.utils.fromJsonArgs

@Composable
fun TransactionDetailsNavGraph(
    modifier: Modifier,
    navController: NavHostController,
    transactionModel: TransactionModel?,
) {
    AppNavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = Screens.TransactionDetails.route,
    ) {
        composable(route = Screens.TransactionDetails.route) {
            TransactionDetailsScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                transactionModel = transactionModel,
            )
        }
        composable(
            route = Screens.InitialSuppliersHistory.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_JSON) { type = NavType.StringType }
            )
        ) {
            val json = it.arguments?.getString(Screens.ARG_KEY_JSON)

            SuppliersHistoryScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                transactionModel = json?.fromJsonArgs<TransactionModel>(),
                isInitial = true,
            )
        }
        composable(
            route = Screens.SuppliersHistory.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_JSON) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            val json = it.arguments?.getString(Screens.ARG_KEY_JSON)

            SuppliersHistoryScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                transactionModel = json?.fromJsonArgs<TransactionModel>(),
                isInitial = false,
            )
        }
    }
}