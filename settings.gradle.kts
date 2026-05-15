/**
 * Root settings for an Aniyomi-style extension monorepo.
 *
 * This mirrors the layout used by https://github.com/aniyomiorg/aniyomi-extensions:
 * - `:core` — shared Android library merged into every extension APK
 * - `:src:<lang>:<name>` — one Gradle module per installable extension
 *
 * For local work you usually keep every extension module enabled. CI often loads a subset.
 */
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

rootProject.name = "aniyomi-extension-template"

apply(from = "repositories.gradle.kts")

include(":core")

// -----------------------------------------------------------------------------
// Individual extension modules live under: src/<lang>/<extensionFolder>/
// -----------------------------------------------------------------------------
include(":src:en:tnaflix")
include(":src:en:eporner")
include(":src:en:xhamster")
include(":src:en:spankbang")
include(":src:en:bigfuck")

// If you add more extensions later, copy the folder pattern and add another line:
// include("src:en:myothersource")
