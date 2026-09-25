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
package com.lacra.presentation.routing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.lacra.R
import com.lacra.base.ObserveEffects
import com.lacra.presentation.auth.AuthActivity
import com.lacra.presentation.main.MainActivity
import com.lacra.presentation.ui.theme.ColorMidnightBlue
import com.lacra.presentation.ui.theme.TextStyleSplash1
import com.lacra.presentation.ui.theme.TextStyleSplash2
import com.lacra.presentation.ui.theme.LacraTraceTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel

class RoutingActivity : ComponentActivity() {
    private lateinit var viewModel: RoutingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        splash.setKeepOnScreenCondition { false }

        setContent {
            viewModel = getViewModel()

            if (viewModel != null) {
                ObserveEffects(viewModel) { effect ->
                    when (effect) {
                        RoutingContract.Effect.NavigateAuth -> {
                            AuthActivity.openAuth(this@RoutingActivity)
                        }
                        RoutingContract.Effect.NavigateMain -> {
                            MainActivity.openMain(this@RoutingActivity)
                        }
                    }
                }
            }

            LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
                viewModel.setEvent(RoutingContract.Event.OnCreate)
            }

            SplashScreen()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        SplashScreen()
    }
}

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = TextStyleSplash1,
                color = ColorMidnightBlue
            )
            Text(
                text = stringResource(R.string.what_is_my_origin),
                style = TextStyleSplash2,
                color = ColorMidnightBlue
            )
        }
    }
}