plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlinx-serialization")
    id("kotlin-kapt")
    id("maven-publish")
}

android {
    namespace = "com.neptune.klat_uikit_android"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.adxcorp"
                artifactId = "klat-uikit-android"
                version = "0.0.1"
            }
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    api("androidx.core:core-ktx:1.1.0")
    api("androidx.appcompat:appcompat:1.3.0")
    api("com.google.android.material:material:1.3.0")
    api("androidx.constraintlayout:constraintlayout:2.1.0")

    // Glide
    api("com.github.bumptech.glide:glide:4.11.0")

    // Kotlinx Serialization
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    api("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    // Retrofit
    api("com.squareup.retrofit2:retrofit:2.9.0")

    // OkHttp
    api("com.squareup.okhttp3:okhttp:4.9.3")
    api("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // ViewModels
    api("androidx.activity:activity-ktx:1.5.0")
    api("androidx.fragment:fragment-ktx:1.2.0")

    api("com.github.adxcorp:talkplus-android:1.0.0")
    api("com.github.chrisbanes:PhotoView:2.0.0")
}