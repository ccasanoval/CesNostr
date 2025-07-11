plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.cesoft.cesnostr"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cesoft.cesnostr"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    /// Modules
    implementation(project(":data"))
    implementation(project(":domain"))

    /// Nostr
    //implementation(libs.nostr.sdk)//TODO: Moved to data, delete here?
    //implementation("net.java.dev.jna:jna:5.17.0@aar")//Don't replace with catalog, problem with @aar

    // DI
    implementation(libs.hilt.android)
    implementation (libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    /// VMI
    implementation(libs.mvi)
    implementation(libs.mvi.compose)

    /// Navigation
    implementation(libs.androidx.navigation.compose)

    /// Image Loader
    implementation(libs.coil.compose)

    /// Scan QR code (https://github.com/G00fY2/quickie)
    implementation(libs.quickie.bundled)
    /// QR code image creator (https://github.com/g0dkar/qrcode-kotlin)
    //implementation("io.github.g0dkar:qrcode-kotlin:4.4.1")//TODO: Moved to data, delete here
}