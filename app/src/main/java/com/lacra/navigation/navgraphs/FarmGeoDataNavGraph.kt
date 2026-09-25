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
import androidx.navigation.compose.composable
import com.lacra.domain.createtransaction.models.CreateTransactionModel
import com.lacra.domain.transactions.models.TransactionModel
import com.lacra.navigation.Screens
import com.lacra.presentation.createtransaction.geodata.FarmGeoDataScreen
import com.lacra.presentation.createtransaction.geodata.map.MapScreen
import com.lacra.presentation.createtransaction.geodata.qr.QrScanScreen
import com.lacra.presentation.createtransaction.geodata.uploadfile.UploadFileScreen
import com.lacra.presentation.ui.components.AppNavHost

@Composable
fun FarmGeoDataNavGraph(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String,
    transactionModel: TransactionModel? = null,
    createTransactionModel: CreateTransactionModel? = null,
) {
    AppNavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(route = Screens.FarmGeoData.route) {
            FarmGeoDataScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                transactionModel = transactionModel,
                createTransactionModel = createTransactionModel,
            )
        }
        composable(route = Screens.UploadFile.route) {
            UploadFileScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                transactionModel = transactionModel,
                createTransactionModel = createTransactionModel,
            )
        }
        composable(route = Screens.Map.route) {
            MapScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
            )
        }
        composable(route = Screens.QrScan.route) {
            QrScanScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                transactionModel = transactionModel,
            )
        }
    }
}