import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    jvm("desktop")
    
    js(IR) {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation("androidx.room:room-runtime:2.6.1")
                implementation("androidx.room:room-ktx:2.6.1")
                implementation("com.google.android.gms:play-services-location:21.0.1")
                implementation("org.osmdroid:osmdroid-android:6.1.18")
                implementation("androidx.activity:activity-compose:1.8.2")
                implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
                implementation("com.google.android.material:material:1.11.0")
            }
        }
        
        val desktopMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
                implementation(compose.desktop.common)
                implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
            }
        }
    }
}

android {
    namespace = "com.activemap.shared"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 26
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    buildFeatures {
        compose = true
    }
}
