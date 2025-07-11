plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    //
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.cesoft.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /// Modules
    implementation(project(":domain"))

    /// DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Preferences
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.security.crypto)

    /// Nostr
    implementation(libs.nostr.sdk)

    /// Scan QR code (https://github.com/G00fY2/quickie)
    implementation(libs.quickie.bundled)
    /// QR code image creator (https://github.com/g0dkar/qrcode-kotlin)
    implementation(libs.qrcode.kotlin)
}