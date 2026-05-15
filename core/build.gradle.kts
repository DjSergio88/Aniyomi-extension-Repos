plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "eu.kanade.tachiyomi.animeextension.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    compileOnly(libs.aniyomi.lib)
}
