plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ovelin.admin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ovelin.admin"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // ====== غيّرها لو دومين السيرفر مختلف قبل البناء ======
        buildConfigField("String", "API_BASE_URL", "\"https://ovelinmall.com\"")
        buildConfigField("String", "ADMIN_PUSH_KEY", "\"2d153c7800cd05a9f908fb7bc6dc8510d8df0ce4cbf590ef7da6fd1baba6f841\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}
