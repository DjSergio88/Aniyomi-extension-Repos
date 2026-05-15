# Build guide — Aniyomi extension template

This document is the **hands-on companion** to `README.md`. It focuses on getting **Gradle sync**, **`assembleDebug`**, and **distribution** right on a clean machine.

---

## Prerequisites

| Requirement | Notes |
|-------------|------|
| **Android Studio** | Hedgehog (2023.1.1) or newer recommended. |
| **Android SDK** | Install **SDK Platform 34** + **Build-Tools 34** (SDK Manager). This template uses `compileSdk = 34` in `buildSrc/src/main/kotlin/AndroidConfig.kt`. |
| **JDK for Gradle** | **JDK 17** runs the Android Gradle Plugin used here. Android Studio bundles a JBR; for CLI builds set `JAVA_HOME` to that JBR (see below). |

### CLI: point `JAVA_HOME` at Android Studio’s JBR (Windows example)

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
```

macOS / Linux (typical Studio install paths vary):

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

---

## Gradle sync (Android Studio)

1. Open the **repository root** (the folder that contains `settings.gradle.kts`).
2. Let the IDE import the Gradle project. If prompted, install missing SDK platforms or build-tools.
3. Use **File → Sync Project with Gradle Files** whenever you change `settings.gradle.kts`, `buildSrc`, or version catalogs.

### Common sync issues

| Symptom | Fix |
|---------|-----|
| **“SDK XML version 4” / platform tools mismatch** | Update **Android SDK Command-line Tools** in SDK Manager so `sdkmanager` matches your Studio generation. |
| **`JAVA_HOME` / toolchain errors** | Ensure Gradle runs on **JDK 17+** (Studio’s embedded JBR is fine). |
| **Dependency resolution failures** | Confirm `settings.gradle.kts` includes `maven("https://jitpack.io")` (this template does). Some upstream libraries (for example Injekt) are fetched from JitPack; coordinates live in `gradle/libs.versions.toml`. |

---

## Build outputs (where the APKs are)

### Debug APK (unsigned / debug-signed)

```bash
./gradlew :src:en:exampletemplate:assembleDebug
```

**Output directory (this module):**

```text
src/en/exampletemplate/build/outputs/apk/debug/
```

Typical file name pattern:

```text
aniyomi-en.exampletemplate-v14.1-debug.apk
```

### Release APK (for GitHub / sideload)

```bash
./gradlew :src:en:exampletemplate:assembleRelease
```

**Output directory:**

```text
src/en/exampletemplate/build/outputs/apk/release/
```

Typical file name pattern:

```text
aniyomi-en.exampletemplate-v14.1.apk
```

This template’s `common.gradle` signs **release** with your **`signingkey.jks`** when present; otherwise it falls back to **debug keys** so beginners can still produce an installable APK.

`applicationId` is recorded in `output-metadata.json` next to each APK. For this template it should be:

```text
eu.kanade.tachiyomi.animeextension.en.exampletemplate
```

---

## Signing release builds (distribution-quality)

1. Generate a keystore (keep it private; never commit it):

```bash
keytool -genkeypair -v -keystore signingkey.jks -alias aniyomiextensions -keyalg RSA -keysize 2048 -validity 10000
```

2. Place `signingkey.jks` in the **repository root** (same folder as `settings.gradle.kts`).

3. Export secrets for the Gradle process:

```bash
export KEY_STORE_PASSWORD="…"
export ALIAS="aniyomiextensions"
export KEY_PASSWORD="…"
```

On Windows PowerShell:

```powershell
$env:KEY_STORE_PASSWORD = "…"
$env:ALIAS = "aniyomiextensions"
$env:KEY_PASSWORD = "…"
```

4. Run `assembleRelease` again.

---

## Publishing on GitHub Releases

1. Bump **`extVersionCode`** in `src/en/exampletemplate/build.gradle.kts` for every public release (Aniyomi uses this integer to detect upgrades).
2. Build **`assembleRelease`** with your release keystore.
3. GitHub → **Releases → Draft a new release** → attach the `.apk` → publish.
4. Update your hosted **`index.min.json`** / **`repository.json`** so the `apk` filename, `version`, and `code` fields match the new artifact.

---

## Connecting the repository to Aniyomi

Aniyomi extension repositories are just **HTTPS URLs** to a JSON index (commonly named `index.min.json`).

### 1) Host the index + APKs

Typical patterns:

- **`repo` branch** on GitHub containing `index.min.json` + `.apk` files (very common in the ecosystem).
- **GitHub Pages** or any static file host (S3, Cloudflare R2, etc.).

### 2) Use a **raw** URL

Example shape (replace user/repo/branch/path):

```text
https://raw.githubusercontent.com/<USER>/<REPO>/<BRANCH>/index.min.json
```

### 3) Add the repo inside Aniyomi

**Settings → Browse → Extension repos → Add** → paste the raw `index.min.json` URL.

### 4) Keep JSON valid and consistent

This repo ships:

- `index.min.json` — minified machine-readable index.
- `repository.json` — same content, pretty-printed for humans / tooling.

Both must remain **valid JSON arrays** of extension objects. After changing sources or `applicationId`, re-check:

- `pkg` matches the Gradle `applicationId`.
- `sources[]` entries align with what your factory registers (`ExampleTemplateFactory` → five catalogue slots in this template).

---

## Verify module wiring (sanity checklist)

| Check | Expected |
|-------|----------|
| `settings.gradle.kts` | Contains `include("src:en:exampletemplate")` (and `:core`). |
| `src/en/exampletemplate/build.gradle.kts` | `extClass` points at `ExampleTemplateFactory` (leading `.` is required by `common.gradle`). |
| Kotlin | `ExampleTemplateFactory` implements `AnimeSourceFactory` and returns all sources you want exposed. |
| Build | `./gradlew :src:en:exampletemplate:assembleDebug` succeeds. |

---

## Quick reference commands

```powershell
# Windows: one-shot debug build
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat :src:en:exampletemplate:assembleDebug
```

```bash
# macOS / Linux
./gradlew :src:en:exampletemplate:assembleDebug
```

---

## Need more architecture context?

See upstream documentation:

- [`aniyomiorg/aniyomi-extensions` contributing guide](https://github.com/aniyomiorg/aniyomi-extensions/blob/master/CONTRIBUTING.md)
- [`aniyomiorg/extensions-lib`](https://github.com/aniyomiorg/extensions-lib) (compile-time API surface)
