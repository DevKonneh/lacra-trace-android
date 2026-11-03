# LACRA Trace - Application identity and configuration

This file separates **display branding** from **technical application identity**.
The application id was switched to `lr.gov.lacra.trace` at LACRA's request (Option B below).
Nothing here was invented: where a value is needed from LACRA it is listed as REQUIRED.

## 1. Current technical identity

| Item | Value | Where |
|---|---|---|
| Namespace / Kotlin package | `lr.gov.lacra.trace` (sources under `app/src/main/java/lr/gov/lacra/trace`) | `app/build.gradle` -> `android.namespace` |
| applicationId (release) | `lr.gov.lacra.trace` | `app/build.gradle` -> `defaultConfig.applicationId` |
| applicationId (debug) | `lr.gov.lacra.trace.dev` (`applicationIdSuffix ".dev"`, versionName suffix `-dev`) | `buildTypes.debug` |
| versionCode / versionName | 76 / 1.15 | `defaultConfig` |
| minSdk / targetSdk / compileSdk | 26 / 36 / 36 | `defaultConfig` |
| Launcher activity | `.presentation.routing.RoutingActivity` | `AndroidManifest.xml` |
| Firebase | Analytics, Crashlytics, Cloud Messaging, Remote Config | `app/google-services.json` per build type (git-ignored) |
| Google sign-in | Credential Manager + Web OAuth client id | `config/secrets.properties` (`*_GOOGLE_AUTH_CLIENT_ID`) |
| Maps | Google Maps SDK key | `config/secrets.properties` (`*_MAPS_API_KEY`) |
| Backend | `BASE_URL`, `TEST_BASE_URL` | `config/build.properties` (git-ignored) |
| Deep links / content providers | none declared in the manifest | - |

## 2. Application id: Option B applied (separate LACRA release)

`applicationId` and the Kotlin package/namespace are now `lr.gov.lacra.trace` (debug `lr.gov.lacra.trace.dev`).
SharedPreferences files (`lacra_trace_preferences`, `lacra_trace_auth_prefs`) and the notification channel id
(`lacra_trace_notification_channel`) were renamed with it.

Consequences:
* Android treats this as a **different app** from the WHIMO build (`com.whimo`): no in-place upgrade, a new
  Play listing, and **no local data carry-over** (Room DB, prefs, offline pending transactions, login session).
  Anyone testing an older APK must sync offline transactions there first.
* Firebase client, OAuth clients, Maps key restrictions and FCM tokens must be created for the new id (list below).
* The git-ignored placeholder `google-services.json` files use the new package names but contain no real credentials.

If LACRA ever needs to update an existing listing instead (Option A), the id would have to go back to that listing's id
and be signed with its upload key.

Follow-ups for the new id:
1. **Firebase**: register `lr.gov.lacra.trace` (+ `.dev`) in a LACRA-owned Firebase project (or add to the
   existing one if the owner agrees); put the resulting files at `app/src/release/google-services.json` and
   `app/src/debug/google-services.json`. Add SHA-1 and SHA-256 of every signing key.
2. **FCM**: backend must hold FCM credentials for the new project/app; device tokens are per-app and are not migrated.
3. **Google sign-in**: create an Android OAuth client per (package, SHA-1) and a Web client; set
   `RELEASE_GOOGLE_AUTH_CLIENT_ID` / `DEBUG_GOOGLE_AUTH_CLIENT_ID`. The backend must accept that Web client id as an audience.
4. **Maps**: restrict the Maps key to the new package + SHA-1 pairs.
5. **Remote Config**: re-create parameters (e.g. `registration_phone_region_policy`) in the new Firebase project; defaults live in `res/xml/remote_config_defaults.xml`.
6. **Signing**: new upload key owned by LACRA; enrol in Play App Signing.
7. **Backend**: confirm the API does not allowlist the old package name / Play Integrity / app signature.
8. **Data migration**: none possible between package ids. Plan a cut-over where users sync all offline transactions in the old app first.
9. `FileProvider`/content providers/deep links: none in this app, nothing to update.

## 3. Values LACRA must supply (not guessed)

| Setting | Key | Status |
|---|---|---|
| API base URL | `release_base_url`, `debug_base_url` | REQUIRED. Local builds use `https://api.invalid/` placeholders. |
| Test-environment API URL | `release_test_base_url`, `debug_test_base_url` | REQUIRED (the app has a practice "Test Environment" feature). |
| Default country / map centre | `*_default_country_code` (`LR`), `*_default_location_latitude/longitude` | Suggested values for Liberia are used locally (LR, 6.3004, -10.7969 = Monrovia, approximate); **confirm**. |
| Terms of use URL | `release_terms_of_use` | REQUIRED. Not invented. |
| Support / feedback e-mail | `release_feedback_email` | REQUIRED. Not invented. |
| Captcha page URL | `release_captcha_url` | REQUIRED if the backend enforces captcha on OTP. |
| Privacy policy URL | (Play Console + legal screen) | REQUIRED. |
| Maps API key | `RELEASE_MAPS_API_KEY` | REQUIRED. |
| Google Web OAuth client id | `RELEASE_GOOGLE_AUTH_CLIENT_ID` | REQUIRED if Google sign-in is to be offered. |
| `google-services.json` | per build type | REQUIRED (git-ignored). |
| Release keystore | `config/signing.properties` | REQUIRED. Never commit. |
| Phone-region allow-list | Remote Config `registration_phone_region_policy` | LACRA decision. The bundled default lists AO, GT, HN, MZ, SL, CH, BR, CO, SV, MY, NI, PY, PE and **does not include Liberia (LR)** - see warning below. |

> **Behaviour warning (not changed by this task):** `RegistrationPhoneRegionPolicy` defaults come from the
> upstream project and do not list `LR`. Registration with a Liberian phone number would be blocked
> unless Remote Config (or the defaults XML) is updated. This is a business/config change, so it was
> **not** made as part of the rebrand. Email registration is unaffected.

## 4. Backend compatibility

No API route, request/response model, header, QR payload, GeoJSON parsing, Room entity, or
SharedPreferences key was altered. The only wire-visible change is the *file name* used when the
user exports a supplier-history file (`LACRA-Trace-<id>.*` instead of `Whimo-<id>.*`), which is a
local file and not sent to the backend.
