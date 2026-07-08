import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-parcelize")
}

kotlin {
    androidTarget {
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("androidx.activity:activity-compose:1.8.2")
                implementation("androidx.compose.ui:ui:1.6.0")
                implementation("androidx.compose.material3:material3:1.2.0")
                implementation("androidx.compose.ui:ui-tooling-preview:1.6.0")
                implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
                implementation("androidx.navigation:navigation-compose:2.7.7")
                implementation("com.google.android.gms:play-services-location:21.0.1")
                implementation("org.osmdroid:osmdroid-android:6.1.18")
                implementation("androidx.room:room-runtime:2.6.1")
                implementation("androidx.room:room-ktx:2.6.1")
                implementation("androidx.room:room-compiler:2.6.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }
    }
}

android {
    namespace = "com.activemap.android"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.activemap.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    buildFeatures {
        compose = true
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
