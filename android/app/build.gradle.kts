// Water Tanks Monitor - Demo app module
// woodyouloveit.com
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Release signing comes from environment variables set by tools/build-android.ps1 (never stored in the project)
val keystoreFile: String? = System.getenv("ANDROID_KEYSTORE_FILE")

android {
    namespace = "com.woodyouloveit.wtms.demo"
    compileSdk = 35

    defaultConfig {
        // Separate ID, so the demo installs next to the real Water Tanks Monitor app
        applicationId = "com.woodyouloveit.wtms.demo"
        minSdk = 26
        targetSdk = 35
        // Override from the build script: -PversionName=1.1.0 -PversionCode=2
        versionName = (project.findProperty("versionName") as String?) ?: "1.0.0"
        versionCode = (project.findProperty("versionCode") as String?)?.toIntOrNull() ?: 1
    }

    signingConfigs {
        if (keystoreFile != null) {
            create("release") {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (keystoreFile != null) signingConfig = signingConfigs.getByName("release")
        }
        debug {
            // Same app ID as release, so the build script can install and launch either
        }
    }

    buildFeatures { buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    // The demo page and its fonts are already compressed where it matters
    androidResources { noCompress += listOf("woff2") }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.webkit:webkit:1.12.1")
}
