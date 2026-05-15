# Aniyomi extension template (Kotlin)

Step-by-step build, signing, releases, and Aniyomi repo wiring: see **[BUILD_GUIDE.md](BUILD_GUIDE.md)**.

This repository is a **minimal, GitHub-ready template** that follows the same Gradle layout as upstream [`aniyomiorg/aniyomi-extensions`](https://github.com/aniyomiorg/aniyomi-extensions): `:core` plus one module under `src/<lang>/<name>/`.

It is intentionally **safe-by-default**:

- All network calls target IANA **`https://example.com/`** placeholder paths only.
- **No** third-party streaming sites are referenced in code, manifests, or repo metadata.

> **Note about “five sources”:** This template ships **five independent catalogue slots** (`slot1` … `slot5`) inside one APK so you can practice multi-source factories. I cannot help name, brand, or scaffold sources around adult websites; fork this template and rename the slots for whatever **legal** catalogue you are authorized to integrate.

## Repository layout (matches upstream conventions)

```text
build.gradle.kts              # Root buildscript (AGP + Kotlin + serialization classpath)
settings.gradle.kts           # Includes :core and :src:en:exampletemplate
common.gradle                 # Shared Android application config for every extension module
core/                         # Tiny Android library merged into each extension APK
src/en/exampletemplate/       # One installable extension module (English / “exampletemplate”)
  AndroidManifest.xml
  build.gradle.kts            # extension metadata: extName, extClass, extVersionCode, isNsfw
  res/                        # launcher icons + high-res web icon
  src/eu/kanade/tachiyomi/animeextension/en/exampletemplate/
```

## Extension metadata (where it lives)

| Field | File | Meaning |
|------|------|--------|
| **Extension display name** | `src/en/exampletemplate/build.gradle.kts` (`extName`) | Shown as the installed APK name in Aniyomi |
| **Entry class** | same (`extClass`) | Must match your `AnimeSourceFactory` / `AnimeSource` class |
| **Version code** | same (`extVersionCode`) | Integer that must increase for every release |
| **NSFW flag** | same (`isNsfw`) | `false` by default; set `true` only if your real catalogue requires it |
| **Language** | folder `src/en/...` + each source’s `lang` | `en` here is the ISO 639-1 language bucket |
| **Per-source title** | Kotlin `override val name` | Shown inside Aniyomi’s Sources list |

## Build a debug APK (Android Studio or CLI)

### Android Studio

1. Install **Android Studio Hedgehog+** with Android SDK **34** (or newer) and a JDK **17** toolchain.
2. Open this folder as a project and wait for Gradle sync.
3. Select module **`src:en:exampletemplate`** (or your added module) in the run configuration dropdown.
4. Use **Build → Make Project**, then **Build → Build Bundle(s) / APK(s) → Build APK(s)**.

### Command line

```bash
./gradlew :src:en:exampletemplate:assembleDebug
```

Outputs are under:

```text
src/en/exampletemplate/build/outputs/apk/debug/
```

## Build & sign a release APK (for distribution)

1. Create a keystore once (keep it private):

```bash
keytool -genkeypair -v -keystore signingkey.jks -alias aniyomiextensions -keyalg RSA -keysize 2048 -validity 10000
```

2. Put `signingkey.jks` in the **repository root** (this template’s `common.gradle` already looks for that path).
3. Export these environment variables before running Gradle:

```bash
export KEY_STORE_PASSWORD="…"
export ALIAS="aniyomiextensions"
export KEY_PASSWORD="…"
```

4. Build:

```bash
./gradlew :src:en:exampletemplate:assembleRelease
```

If `signingkey.jks` is missing, this template falls back to **debug signing** for `release` so beginners can still compile.

## Publish GitHub Releases (host APKs)

1. Bump `extVersionCode` in `src/en/exampletemplate/build.gradle.kts` (required for Aniyomi to see updates).
2. Build a **signed** `assembleRelease` APK.
3. On GitHub: **Releases → Draft a new release → Attach the `.apk` → Publish**.
4. Keep a **stable download URL** for each APK name pattern (users and index files reference the file name).

## Connect this repo to Aniyomi (Extension Repos)

Aniyomi reads a **JSON index** hosted on the raw branch (commonly `repo/index.min.json`).

This template includes:

- `index.min.json` — the machine-readable list Aniyomi consumes.
- `repository.json` — the same payload, pretty-printed for humans/tools.

### Typical hosting pattern

1. Commit built APKs to a **`repo/` branch** (or GitHub Pages / any static host).
2. Ensure `index.min.json` lists each APK with correct `pkg`, `version`, `code`, and nested `sources[]`.
3. In Aniyomi: **Settings → Browse → Extension repos → Add** the raw URL to your `index.min.json`.

Replace `YOUR_GITHUB_USER` / `YOUR_REPO` in those JSON files before publishing.

### About `sources[].id` in `index.min.json`

Aniyomi identifies each source with a **stable numeric id** derived from the source implementation (see `extensions-lib` / `generateId`). The ids in this template’s JSON files are **placeholders for documentation only**. After your first working build, update them to match what Aniyomi shows for your sources (or regenerate the index using the same workflow upstream repos use in CI).

## Regenerating icons

Replace files under `src/en/exampletemplate/res/mipmap-*` and `web_hi_res_512.png` with real artwork. Upstream recommends the Android Asset Studio launcher icon generator linked in Aniyomi’s contributing guide.

## Command-line builds need a JDK

If `gradlew` fails with **JAVA_HOME is not set**, install a **JDK 17** and point `JAVA_HOME` at it (Android Studio bundles one under its installation directory on Windows).

## Licenses

This template is scaffolding only. When you fork it for a real website, you are responsible for that site’s terms of service and for complying with applicable law.
