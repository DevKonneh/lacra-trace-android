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
package lr.gov.lacra.trace.navigation.bottombar

import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.navigation.Screens

sealed class BottomNavItem(
    val screen: Screens,
    val icon: Int,
    val label: Int,
) {

    data object Home : BottomNavItem(
        screen = Screens.Home,
        icon = R.drawable.ic_home,
        label = R.string.home
    )

    data object Balances : BottomNavItem(
        screen = Screens.Balances,
        icon = R.drawable.ic_balances,
        label = R.string.balances
    )

//    data object CreateTransaction : BottomNavItem(
//        screen = Screens.CreateTransaction,
//        icon = R.drawable.ic_add_transaction,
//        label = R.string.transactions
//    )

    data object Settings : BottomNavItem(
        screen = Screens.Settings,
        icon = R.drawable.ic_settings,
        label = R.string.settings
    )

    companion object {
        val MainNavItems = listOf(
            Home,
            Balances,
//            CreateTransaction,
            Settings,
        )
    }
}


