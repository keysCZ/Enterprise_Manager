plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.milfhey.enterprisemanager"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.milfhey.enterprisemanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    implementation("com.google.firebase:firebase-auth:22.0.0")
    implementation("com.google.firebase:firebase-database:20.2.1")
    implementation("com.google.firebase:firebase-core:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.activity)
    implementation(libs.monitor)
    implementation(libs.androidx.junit)
    
}

// Appliquez le plugin Google Services
apply(plugin = "com.google.gms.google-services")