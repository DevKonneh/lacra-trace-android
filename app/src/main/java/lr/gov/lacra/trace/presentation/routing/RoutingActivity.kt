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
package lr.gov.lacra.trace.presentation.routing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.base.ObserveEffects
import lr.gov.lacra.trace.presentation.auth.AuthActivity
import lr.gov.lacra.trace.presentation.main.MainActivity
import lr.gov.lacra.trace.presentation.ui.theme.ColorBrandGreen
import lr.gov.lacra.trace.presentation.ui.theme.ColorGold
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleSplash1
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleSplash2
import lr.gov.lacra.trace.presentation.ui.theme.TextStyleSplashMotto
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme
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

/**
 * In-app splash, shown right after the system splash (androidx.core-splashscreen).
 *
 * The logo is drawn from the SAME bitmap as the system splash icon (288 dp canvas, centred on screen)
 * so the hand-over between the two has no visible jump. Text is laid out around that fixed centre:
 * name + gold rule + institution below the emblem, the motto at the bottom edge.
 */
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBrandGreen),
    ) {
        Image(
            modifier = Modifier
                .size(288.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = stringResource(R.string.institution_name),
        )

        // The emblem disc ends 94 dp below the centre; start the text just under it.
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 126.dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = TextStyleSplash1,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .width(56.dp)
                    .height(3.dp)
                    .background(ColorGold, RoundedCornerShape(2.dp)),
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.institution_name),
                style = TextStyleSplash2,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }

        Text(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
            text = stringResource(R.string.institution_motto),
            style = TextStyleSplashMotto,
            color = ColorGold,
            textAlign = TextAlign.Center,
        )
    }
}
