plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.phamthiphuonguyen_delightshop"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.phamthiphuonguyen_delightshop"
        minSdk = 24
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
    implementation(libs.firebase.database)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("io.coil-kt:coil-compose:2.2.2") // Check for the latest version
    implementation("com.google.accompanist:accompanist-pager:0.28.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.28.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0") // Update to the latest version
    implementation("androidx.compose.runtime:runtime-livedata:1.5.0") // Ensure it matches with Compose version
    implementation("com.github.bumptech.glide:glide:4.15.0") // Check for the latest Glide version
    implementation("com.google.code.gson:gson:2.9.1") // Latest version is fine
    implementation("androidx.compose.foundation:foundation:1.5.0")
    implementation ("com.sun.mail:android-mail:1.6.2")
    implementation ("com.sun.mail:android-activation:1.6.2")
    implementation ("com.cloudinary:cloudinary-android:2.1.0")
    implementation ("androidx.compose.ui:ui-text-google-fonts:1.6.0")
}