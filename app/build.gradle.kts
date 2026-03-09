import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("plugin.serialization") version "2.0.0"
}

android {
    namespace = "com.app.assistant"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.assistant"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Read local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(FileInputStream(localPropertiesFile))
        }

        // Define BuildConfig fields with dummy values
        buildConfigField("String", "YOUTUBE_API_KEY", "\"dummy_key\"")
        buildConfigField("String", "GROQ_API_KEY", "\"dummy_key\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Material3 de bază - DOAR atât
    implementation("androidx.compose.material3:material3:1.2.1")
    // implementation("androidx.compose.material3:material3-window-size-class:1.2.1") // Comentează dacă nu e nevoie
    // implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.2.1") // Comentat - nu există

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.saveable)

    // Networking (dacă vrei să păstrezi)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    
    // Markdown (dacă vrei să păstrezi)
    implementation("org.commonmark:commonmark:0.24.0")
    
    // Image Loading (dacă vrei să păstrezi)
    implementation("io.coil-kt.coil3:coil-compose:3.0.0-rc02")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0-rc02")
    
    // Kotlin Reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    
    // Google Play Services (dacă vrei să păstrezi)
    implementation(libs.play.services.location)
    
    // Security
    implementation("androidx.security:security-crypto:1.1.0")
    
    // MediaPipe (dacă vrei să păstrezi)
    implementation("com.google.mediapipe:tasks-text:0.10.29")
    
    // Play Updates (dacă vrei să păstrezi)
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Testing
    testImplementation(libs.junit)
    
    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.ui.test.android)
}
