import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.gradle.agp)
        classpath(libs.gradle.kotlin)
        classpath(libs.gradle.kotlin.serialization)
    }
}

// Workaround for missing buildSrc classes during sync
extra.set("AndroidConfig", mapOf(
    "compileSdk" to 34,
    "minSdk" to 21,
    "targetSdk" to 34,
    "namespace" to "eu.kanade.tachiyomi.animeextension"
))

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs += listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            )
        }
    }
}
