# LACRA Trace - Branding Guide

**Liberia Agriculture Commodity Regulatory Authority (LACRA)** - agricultural commodity
supply chain traceability app. This repository is a rebrand of the open-source
`EuropeanForestInstitute/whimo-android` (WHIMO) application. Functionality, business rules,
API contracts and data formats are unchanged; only identity and visual styling were changed.

> The **logo is the official LACRA logo** supplied by LACRA (`branding-source/lacra-logo-official.jpg`,
> 512x512 JPEG on white). The **UI palette is derived from that logo** (colours sampled from the artwork:
> leaf/palm green, cocoa-pod gold, coffee brown); it is not a confirmed LACRA brand manual.
> No seal or emblem was drawn by us. Only the Android front end is rebranded; the backend/admin is a later step.

## 1. What changed

| Area | Change |
|---|---|
| App name | `app_name` = **LACRA Trace** (en, es, fr). Debug build label = `LACRA Trace DEV`. |
| Institution | `institution_name` = Liberia Agriculture Commodity Regulatory Authority (not translated, proper name). Shown on splash, login/registration header, Settings > About. |
| Splash | Brand-green background, official emblem on a white disc (`ic_splash_logo`, 288dp canvas / 192dp circle per the Android 12 spec) via `androidx.core:core-splashscreen`; the in-app splash then shows app name, gold rule, institution name and the motto "From Seed to Table: Regulating for Excellence" (the motto is taken from the logo). |
| Launcher | Adaptive icon (`mipmap-anydpi/ic_launcher.xml`): green background, emblem on a white disc inside the 66dp safe zone, monochrome layer for Android 13+ themed icons. The debug variant adds an orange "DEV" ribbon. |
| Notifications | Single-colour 24dp status-bar icon `ic_stat_notification` (alpha-only silhouette of the inner emblem), accent `notification_accent` (brand green). |
| Theme | Compose `LacraTraceTheme` (was `WhimoTheme`), XML `Theme.LacraTraceApp`. Material3 scheme in `ColorScheme.kt`, palette in `Palette.kt`. |
| Export / share | Exported file names `LACRA-Trace-<id>.geojson/.csv/.zip` (was `Whimo-<id>`); feedback e-mail subject "LACRA Trace App Feedback". |
| Invite text | "Tap to invite a user to LACRA Trace" (and counterparty instruction) in en/es/fr. |
| Removed | The WHIMO tagline "What is my origin" (splash). |

## 2. Where to adjust the look

* `app/src/main/java/lr/gov/lacra/trace/presentation/ui/theme/Palette.kt` - every Compose colour.
* `app/src/main/java/lr/gov/lacra/trace/presentation/ui/theme/ColorScheme.kt` - Material3 role mapping
  (buttons, links, tabs, bottom navigation, text-field focus, cards, dialogs, progress, scrim).
* `app/src/main/res/values/colors.xml` - system-level colours (splash, launcher background,
  notification accent, system bars). **Keep in sync with `Palette.kt`.**
* `app/src/main/res/values/themes.xml` - window theme, status/navigation bar, splash theme.
* `ui/theme/TextStyles.kt`, `Typography.kt` - typography (system default font).

### Palette (derived from the logo, NOT an official brand manual)

Colours were sampled from the supplied logo with k-means clustering and then adjusted only where needed for legibility.

| Role | Hex | Meaning in the logo | Notes |
|---|---|---|---|
| Brand green (primary) | `#226C13` | Leaf, palm and the logo's green lettering | white-on-green 6.5:1; also splash / launcher background |
| Green light | `#30BF16` | Bright leaf highlight | decorative / progress only |
| Green dark | `#14400B` | Shade of the green | scrim base, headings on tints |
| Green tint | `#E9F5E5` | - | selected / expanded surfaces |
| Gold | `#F8D92E` | Cocoa pod | decorative (rule under the name, motto on green); never text on white |
| Gold dark (text) | `#7A6200` | - | 5.9:1 on white |
| Brown | `#4A2D13` | Coffee beans, swoosh, ring text | 12.5:1 on white |

Meaning: green = agriculture and regulation, gold = cocoa/commodity value, brown = coffee and soil.
Contrast values come from the WCAG 2.x formula (ad-hoc calculation, not a saved audit).

### Status colours stay independent of the brand

| State | Colour | Contrast (text on badge bg) |
|---|---|---|
| Success / accepted / full | `#1F7A35` on `#EEFAEE` | 5.0:1 (original `#29C229` was 2.2:1) |
| Warning / pending | `#A85A00` on `#FDF7EF` | 4.8:1 (original `#E19C3B` was 2.2:1) |
| Error / rejected | `#C22929` on `#FAEEEE` | 5.1:1 |
| Info / recorded / conditional | `#1F6FA8` on `#EEF6FA` | 4.9:1 |
| Partial traceability / Test environment | purple `#901F82` / `#720062` | unchanged, deliberately not green |
| Incomplete | gray `#4D4D4D` | - |
| Offline / sync | existing offline banner and sync progress dialog, unchanged logic | - |

Status-colour contrast values were computed with the WCAG 2.x formula (ad-hoc); they are not a substitute for an accessibility audit.

Dark mode: the upstream app ships a light scheme only and opts out of forced dark
(`android:forceDarkAllowed=false`). That behaviour is preserved; there is no dark scheme to restyle.

## 3. Logo - how the assets are produced

The only source artwork is the supplied 512x512 JPEG (`branding-source/lacra-logo-official.jpg`).

1. `python3 branding-source/make_master.py` -> `branding-source/lacra-logo-1024-transparent.png`
   (2x Lanczos upscale, white background removed by flood fill from the border, soft edge, 1024x1024).
2. `python3 branding-source/generate_assets.py` -> every launcher, splash, notification, in-app and Play asset below.

Needs Pillow, numpy and scipy; run from the repo root.

> **Honest note:** the 1024px transparent PNG is an **upscale** of the 512px JPEG, not new artwork. It gives the
> correct file size and transparency, but fine detail (ring text, palm fruit) is still as soft as the source.
> A real vector (SVG/PDF) or a native transparent PNG >= 1024px from LACRA is still wanted; drop it in as
> `branding-source/lacra-logo-1024-transparent.png` (skip step 1) and re-run step 2.

| File | Standard | Notes |
|---|---|---|
| `mipmap-anydpi/ic_launcher.xml` + `mipmap-*/ic_launcher_foreground.png` | Adaptive icon, 108dp layers (mdpi 108px ... xxxhdpi 432px), 66dp safe zone | Logo sits on a white disc so the brown ring text stays readable on green |
| `mipmap-*/ic_launcher_monochrome.png` | Android 13+ themed icon | Automatic silhouette; designer review advised |
| `drawable-*/ic_splash_logo.png` | Android 12 splash icon: 288dp canvas, 192dp visible circle | Emblem on white disc |
| `drawable-*/ic_stat_notification.png` | Status-bar icon: 24dp, alpha-only, 20dp artwork | Inner emblem only; approximation |
| `drawable-nodpi/ic_logo.png` | In-app header (`BrandHeader`) | 768px, transparent |
| `docs/play-store/ic_launcher_512.png` | Google Play icon: 512x512, 32-bit PNG | Green background + white disc + logo (Play applies its own mask) |
| `app/src/debug/res/mipmap-*/ic_launcher_dev_foreground.png` | Debug icon with DEV ribbon | Debug build only |
| `values/colors.xml` -> `splash_background`, `launcher_background` | Brand green | Keep equal to `GREEN` in `generate_assets.py` |

Not included: Play feature graphic (1024x500), screenshots, store listing text - see `RELEASE_CHECKLIST.md`.
The splash/icon previews in `docs/previews/` are composites made by script, **not** device screenshots.

## 4. Things deliberately NOT renamed

The package/applicationId, preference file names and notification channel id **were** changed to `lr.gov.lacra.trace` / `lacra_trace_*` at LACRA's request (see `APP_IDENTITY.md`), so old `com.whimo` installs do not upgrade or share data. These were deliberately **not** changed because they would break stored data or backend contracts:

| Item | Value | Why kept |
|---|---|---|
| Room database | unchanged | Schema untouched. |
| QR / GeoJSON parsing | unchanged | Payload format compatibility. The `scanned-by: whimo` line in `QrGeoUtilsTest` is arbitrary test input. |
| Copyright / license | `LICENSE`, source headers: "Copyright (c) 2025 EFI" | MIT requires the notice to be preserved. |
| Remote Config defaults | phone-region policy list | Backend/Firebase-driven behaviour, not branding. |
