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
package lr.gov.lacra.trace.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import lr.gov.lacra.trace.R
import lr.gov.lacra.trace.presentation.ui.theme.LacraTraceTheme

@Preview
@Composable
private fun Preview() {
    LacraTraceTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val termsTag = "TERMS"
            val termsText = stringResource(R.string.terms_of_use)
            val fullText = stringResource(R.string.terms_accept_text, termsText)

            RichTextWithLinks(
                fullText = fullText,
                linkSpecs = listOf(LinkSpec(text = termsText, tag = termsTag)),
                onLinkClick = { tag ->
                    if (tag == termsTag) {}
                }
            )
        }
    }
}

data class LinkSpec(
    val text: String,
    val tag: String = text,
)

@Composable
fun RichTextWithLinks(
    fullText: String,
    linkSpecs: List<LinkSpec>,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    baseColor: Color = MaterialTheme.colorScheme.onSurface,
    linkColor: Color = MaterialTheme.colorScheme.primary,
    underlineLinks: Boolean = true,
    onLinkClick: (tag: String) -> Unit
) {
    val annotated = buildAnnotatedString {
        val ranges = mutableListOf<Triple<Int, Int, LinkSpec>>()

        linkSpecs.forEach { spec ->
            var start = fullText.indexOf(spec.text)
            while (start >= 0) {
                val end = start + spec.text.length
                ranges.add(Triple(start, end, spec))
                start = fullText.indexOf(spec.text, end)
            }
        }

        ranges.sortBy { it.first }

        var currentIndex = 0

        ranges.forEach { (start, end, spec) ->
            if (currentIndex < start) {
                append(fullText.substring(currentIndex, start))
            }

            pushLink(
                LinkAnnotation.Clickable(
                    tag = spec.tag,
                    // Стиль ссылки
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = linkColor,
                            textDecoration = if (underlineLinks) {
                                TextDecoration.Underline
                            } else {
                                TextDecoration.None
                            }
                        )
                    ),
                    linkInteractionListener = { _ ->
                        onLinkClick(spec.tag)
                    }
                )
            )
            append(fullText.substring(start, end))
            pop()

            currentIndex = end
        }

        if (currentIndex < fullText.length) {
            append(fullText.substring(currentIndex))
        }
    }

    Text(
        modifier = modifier,
        text = annotated,
        style = style.copy(color = baseColor)
    )
}