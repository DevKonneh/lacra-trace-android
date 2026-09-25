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
package com.lacra.presentation.transactions.transactiondetails.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lacra.R
import com.lacra.presentation.ui.theme.ColorMulberryPurple
import com.lacra.presentation.ui.theme.ColorSeaBlue
import com.lacra.presentation.ui.theme.ColorSuccess
import com.lacra.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    val items = listOf(
        PieChartItem(stringResource(R.string.partial), 40, ColorMulberryPurple),
        PieChartItem(stringResource(R.string.conditional), 28, ColorSeaBlue),
        PieChartItem(stringResource(R.string.full), 12, ColorSuccess),
    )

    LacraTraceTheme {

        PieChart2(
            items = items,
            chartSize = 80.dp,
            strokeWidth = 10.dp,
            gapSize = 1.dp,
            strokeCapRadius = 4.dp,
        )
    }
}

data class PieChartItem(
    val name: String,
    val value: Int,
    val color: Color,
)

@Composable
fun PieChart2(
    items: List<PieChartItem>,
    chartSize: Dp,
    strokeWidth: Dp = 10.dp,
    gapSize: Dp = 1.dp,
    strokeCapRadius: Dp = 2.dp,
) {
    val density = LocalDensity.current
    val filteredItems = items.filter { it.value > 0 }

    Box(
        modifier = Modifier.size(size = chartSize),//min(chartSize.toPx(), min(size.width, size.height))
        contentAlignment = Alignment.Center,
    ) {

        val mSize = density.run { chartSize.toPx() }
        val mGapSize = density.run { gapSize.toPx() }


        val perimeter = mSize * Math.PI
        val gapDegree = ((mGapSize / perimeter) * 360f).toFloat()

        PieChartBase(
            chartSize = chartSize,
            items = filteredItems,
            strokeWidth = strokeCapRadius,
            gapDegree = gapDegree,
            strokeCap = StrokeCap.Round,
        )

        PieChartBase(
            chartSize = chartSize - strokeCapRadius,
            items = filteredItems,
            strokeWidth = strokeWidth - strokeCapRadius,
            gapDegree = gapDegree,
            strokeCap = StrokeCap.Butt,
        )

        PieChartBase(
            chartSize = chartSize - strokeWidth * 2 + strokeCapRadius * 2,
            items = filteredItems,
            strokeWidth = strokeCapRadius,
            gapDegree = gapDegree,
            strokeCap = StrokeCap.Round,
        )
    }
}

@Composable
private fun PieChartBase(
    items: List<PieChartItem>,
    chartSize: Dp,
    strokeWidth: Dp,
    gapDegree: Float,
    strokeCap: StrokeCap,
) {

    Canvas(modifier = Modifier.size(chartSize)) {
        val total = items.sumOf { it.value }.toFloat()

        val mChartSize = chartSize.toPx()
        var mStrokeWidth = strokeWidth.toPx()
        val perimeter = mChartSize * Math.PI

        val roundCapOffsetDegree = (mStrokeWidth / perimeter * 360f).toFloat()

        val radiusOffset = mStrokeWidth / 2
        val arcSize = Size(mChartSize - mStrokeWidth, mChartSize - mStrokeWidth)
        var startAngle = -90f

        items.forEach {
            var sweepAngle = (it.value / total) * 360f
            var mStrokeCap = strokeCap
            var mGapDegree = gapDegree

            if (mStrokeCap == StrokeCap.Round && sweepAngle < roundCapOffsetDegree + gapDegree) {
                mStrokeCap = StrokeCap.Butt
                mStrokeWidth *= (sweepAngle / (roundCapOffsetDegree + gapDegree))
            }

            sweepAngle -= gapDegree

            if (mStrokeCap == StrokeCap.Round) {
                sweepAngle -= roundCapOffsetDegree
                mGapDegree += roundCapOffsetDegree
            }

            drawArc(
                color = it.color,
                startAngle = startAngle + mGapDegree / 2f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(radiusOffset, radiusOffset),
                size = arcSize,
                style = Stroke(
                    width = mStrokeWidth,
                    cap = mStrokeCap,
                ),
            )

            startAngle += (it.value / total) * 360f
        }
    }
}