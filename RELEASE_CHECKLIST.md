# LACRA Trace - Release checklist

Tick every box before a production release. Items marked (LACRA) cannot be done by the developer alone.

## Identity
- [x] Application id switched to `lr.gov.lacra.trace` (debug `.dev`) - see `APP_IDENTITY.md`. [ ] (LACRA) Confirm this id is the final one before the first Play upload (it cannot be changed later).
- [ ] Register the new id in Firebase / Google OAuth / Maps / FCM (`APP_IDENTITY.md` section 2).
- [ ] Bump `versionCode` / `versionName`.
- [ ] Confirm launcher label reads **LACRA Trace** in en/es/fr (and any new locale).

## Branding assets
- [x] Official logo received (512px JPEG) and applied; 1024px transparent master is an upscale. [ ] (LACRA) Supply a real vector (SVG/PDF) or native transparent PNG >= 1024px and, if they exist, official brand colours; re-run `branding-source/generate_assets.py` (see `BRANDING.md` section 3).
- [ ] Re-check contrast after applying official colours (>= 4.5:1 for text).
- [ ] Verify launcher icon on: round mask, squircle mask, Android 13+ themed icon, API 26-30.
- [ ] Verify splash on Android 12+ and on API 26-30.
- [ ] Verify status-bar notification icon is a white silhouette on Android 8-15.
- [x] Play icon 512x512 generated: `docs/play-store/ic_launcher_512.png` (replace if a native hi-res logo arrives).
- [ ] Play Store: 512x512 icon upload, 1024x500 feature graphic, phone screenshots (taken from a build with official assets), short/full description.

## Signing
- [ ] (LACRA) Create a release keystore (kept offline, backed up); fill `config/signing.properties` locally/CI only.
- [ ] Enrol in Play App Signing; record the **app signing** SHA-1/SHA-256.
- [ ] Add SHA fingerprints to Firebase and to Google OAuth Android clients.
- [ ] Confirm no keystore/passwords are committed (`git ls-files | grep -Ei 'jks|keystore|signing.properties'` -> empty).

## Backend and configuration
- [ ] (LACRA) `config/build.properties`: production `release_base_url`, `release_test_base_url`, terms URL, feedback e-mail, captcha URL, default country/location.
- [ ] (LACRA) `config/secrets.properties`: `RELEASE_MAPS_API_KEY`, `RELEASE_GOOGLE_AUTH_CLIENT_ID`.
- [ ] `app/src/release/google-services.json` from LACRA's Firebase project.
- [ ] Backend FCM credentials configured; push notification test passes.
- [ ] Remote Config: `registration_phone_region_policy` includes `LR` (see `APP_IDENTITY.md` warning); force-update parameters set.
- [ ] Backend accepts the Google Web client id and any package/signature allow-lists.
- [ ] Release build uses HTTPS URLs only, with no `*.invalid` placeholders: `./gradlew :app:assembleRelease` then grep BuildConfig for `invalid`.

## Legal and store
- [ ] (LACRA) Privacy policy URL, terms of use, data-safety form (location, contacts, camera, phone/e-mail, crash/analytics data).
- [ ] (LACRA) Support e-mail / website for the store listing.
- [ ] Keep `LICENSE` and the "Copyright (c) 2025 EFI" headers (MIT). Add LACRA copyright line **only** if LACRA's counsel instructs; do not remove the original.
- [ ] Permissions review: camera (QR scan), location (farm geodata), contacts (counterparty pick), notifications.

## Quality gates
- [ ] `./gradlew :app:testDebugUnitTest` green.
- [ ] `./gradlew :app:lintDebug` - no new errors vs upstream baseline.
- [ ] Manual smoke on a physical device: login, registration, create buy/sell transaction, QR scan, map, offline create then sync, convert commodity, export supplier history, settings, language switch, notifications.
- [ ] Test on small (360dp) and large screens, font scale 1.3, TalkBack basic pass.
- [ ] Internal testing track -> closed -> production; check Play pre-launch report.
