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
package lr.gov.lacra.trace.presentation.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// LACRA Trace palette (single source of truth for Compose UI).
//
// Colours are DERIVED FROM THE OFFICIAL LACRA LOGO (sampled with k-means from the supplied artwork):
//   leaf green  #30BF16  -> brand green family (darkened so white text on it passes WCAG AA)
//   cocoa/palm  #F8D92E  -> gold (decorative accent; never used for text on white)
//   bean brown  #4A2D13  -> brown (ring text, splash caption, headings on tints)
// They are NOT an official LACRA brand manual: if LACRA publishes official values, change them here
// and mirror the system-level ones in res/values/colors.xml (splash, launcher, system bars,
// notification accent) and branding-source/generate_assets.py (GREEN).
// ---------------------------------------------------------------------------

// Gray colors
val ColorGray5 = Color(0XFFF9F9F9)
val ColorGray10 = Color(0XFFE5E5E5)
val ColorGray20 = Color(0XFFCCCCCC)
val ColorGray30 = Color(0XFFB2B2B2)
val ColorGray40 = Color(0XFF999999)
val ColorGray50 = Color(0XFF808080)
val ColorGray60 = Color(0XFF666666)
val ColorGray70 = Color(0XFF4D4D4D)
val ColorGray80 = Color(0XFF333333)
val ColorGray90 = Color(0XFF1A1A1A)
val ColorGray100 = Color(0XFF000000)

// Brand colors: leaf green (logo leaf #30BF16, darkened to #226C13 for contrast)
val ColorBrandGreen = Color(0XFF226C13)       // primary: buttons, links, selected states (white-on 6.5:1)
val ColorBrandGreenLight = Color(0XFF30BF16)  // the logo's own leaf green - decorative only (2.4:1 on white)
val ColorBrandGreenDark = Color(0XFF14400B)   // headings on tinted backgrounds, scrim base
val ColorBrandGreenTint = Color(0XFFE9F5E5)   // soft green surfaces (selected / expanded rows)

// Brand colors: cocoa-pod gold and bean brown from the logo
val ColorBrandBrown = Color(0XFF4A2D13)       // 12.5:1 on white
val ColorGold = Color(0XFFF8D92E)             // decorative (4.6:1 on brand green); use ColorGoldDark for text
val ColorGoldDark = Color(0XFF7A6200)         // 5.9:1 on white
val ColorGoldTint = Color(0XFFFFF7D1)

// Warm neutrals
val ColorSandBrown = Color(0XFFDEDAD4)
val ColorBirchWhite = Color(0XFFECEAE5)

// Test environment (kept purple on purpose: must never be confused with the green brand)
val ColorPlumPurple = Color(0XFF720062)
val ColorMulberryPurple = Color(0XFF901F82)

// Status colors (semantic; deliberately independent from the brand colors).
// Success/warning were darkened from the original palette to reach WCAG AA (4.5:1)
// when used as text on their light badge backgrounds.
val ColorSuccess = Color(0XFF1F7A35)
val ColorWarning = Color(0XFFA85A00)
val ColorWarning10 = Color(0x1AA85A00)
val ColorError = Color(0XFFC22929)
val ColorInfo = Color(0XFF1F6FA8)             // "recorded" / "conditional" states
val ColorIncomplete = ColorGray70             // "incomplete" traceability

// Harvest season badge colors
val ColorHarvestSeasonActiveBackground = Color(0XFFE7F0E7)
val ColorHarvestSeasonActiveContent = Color(0XFF0F6E0F)
val ColorHarvestSeasonPastBackground = Color(0XFFF0EFE7)
val ColorHarvestSeasonPastContent = Color(0XFF6E5C0F)

// Light status backgrounds
val ColorLightInfo = Color(0xFFEEF6FA)
val ColorLightOrange = Color(0xFFFDF7EF)
val ColorLightGreen = Color(0xFFEEFAEE)
val ColorLightRed = Color(0xFFFAEEEE)

// Transparent colors
val ColorBrandGreen10 = Color(0x1A226C13)
val ColorBrandGreen50 = Color(0x80226C13)
val ColorBrandGreenDark30 = Color(0x4D14400B)
val ColorMulberryPurple10 = Color(0X1A901F82)
val ColorInfo10 = Color(0X1A1F6FA8)
val ColorIncomplete10 = Color(0X1A4D4D4D)
val ColorSuccess10 = Color(0X1A1F7A35)
