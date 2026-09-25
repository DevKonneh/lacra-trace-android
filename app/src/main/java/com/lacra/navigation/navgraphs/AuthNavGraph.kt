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
import com.lacra.navigation.Screens
import com.lacra.presentation.auth.createNewPassword.CreateNewPasswordScreen
import com.lacra.presentation.auth.enterCode.EnterCodeScreen
import com.lacra.presentation.auth.enterCode.EnterCodeScreenState
import com.lacra.presentation.auth.forgotPassword.ForgotPasswordScreen
import com.lacra.presentation.auth.login.LoginScreen
import com.lacra.presentation.auth.registration.RegistrationScreen
import com.lacra.presentation.ui.components.AppNavHost

@Composable
fun AuthNavGraph(
    modifier: Modifier,
    navController: NavHostController,
) {
    AppNavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = Screens.LoginScreen.route,
    ) {
        composable(route = Screens.LoginScreen.route) {
            LoginScreen(modifier = modifier.fillMaxSize(), navController = navController)
        }
        composable(route = Screens.RegistrationScreen.route) {
            RegistrationScreen(modifier = modifier.fillMaxSize(), navController = navController)
        }
        composable(
            route = Screens.RegistrationConfirmEmailCodeScreen.route,
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
            route = Screens.RegistrationConfirmPhoneCodeScreen.route,
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
        composable(route = Screens.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(modifier = modifier.fillMaxSize(), navController = navController)
        }
        composable(
            route = Screens.ForgotPasswordConfirmEmailCodeScreen.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_EMAIL) { type = NavType.StringType }
            )
        ) {
            EnterCodeScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                state = EnterCodeScreenState.ForgotPassword,
                phone = it.arguments?.getString(Screens.ARG_KEY_EMAIL),
            )
        }
        composable(
            route = Screens.ForgotPasswordConfirmPhoneCodeScreen.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_PHONE) { type = NavType.StringType }
            )
        ) {
            EnterCodeScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                state = EnterCodeScreenState.ForgotPassword,
                phone = it.arguments?.getString(Screens.ARG_KEY_PHONE),
            )
        }

        composable(
            route = Screens.CreateNewPasswordScreen.route,
            arguments = listOf(
                navArgument(Screens.ARG_KEY_USERNAME) { type = NavType.StringType },
                navArgument(Screens.ARG_KEY_CODE) { type = NavType.StringType },
            )
        ) { backStackEntry ->
            CreateNewPasswordScreen(
                modifier = modifier.fillMaxSize(),
                navController = navController,
                username = backStackEntry.arguments?.getString(Screens.ARG_KEY_USERNAME),
                code = backStackEntry.arguments?.getString(Screens.ARG_KEY_CODE),
            )
        }
    }
}