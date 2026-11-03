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
package lr.gov.lacra.trace.navigation.navgraphs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import lr.gov.lacra.trace.navigation.Screens
import lr.gov.lacra.trace.presentation.auth.enterCode.EnterCodeScreen
import lr.gov.lacra.trace.presentation.auth.enterCode.EnterCodeScreenState
import lr.gov.lacra.trace.presentation.settings.account.AccountInfoScreen
import lr.gov.lacra.trace.presentation.settings.account.editemail.EditEmailScreen
import lr.gov.lacra.trace.presentation.settings.account.editphone.EditPhoneScreen

@Composable
fun AccountNavGraph(
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = Screens.AccountInfo.route,
    ) {
        composable(route = Screens.AccountInfo.route) {
            AccountInfoScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
            )
        }
        composable(
            route = Screens.EditEmail.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_EMAIL) { type = NavType.StringType }
            )
        ) {
            EditEmailScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                email = it.arguments?.getString(Screens.ARG_KEY_EMAIL),
            )
        }
        composable(
            route = Screens.EditPhone.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_PHONE) { type = NavType.StringType }
            )
        ) {
            EditPhoneScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                phone = it.arguments?.getString(Screens.ARG_KEY_PHONE),
            )
        }
        composable(
            route = Screens.ConfirmEmailCodeScreen.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_EMAIL) { type = NavType.StringType }
            )
        ) {
            EnterCodeScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                state = EnterCodeScreenState.Registration,
                email = it.arguments?.getString(Screens.ARG_KEY_EMAIL),
            )
        }
        composable(
            route = Screens.ConfirmPhoneCodeScreen.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_PHONE) { type = NavType.StringType }
            )
        ) {
            EnterCodeScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                state = EnterCodeScreenState.Registration,
                phone = it.arguments?.getString(Screens.ARG_KEY_PHONE),
            )
        }
    }
}