import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.activemap.desktop.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ActiveMap"
            packageVersion = "1.0.0"
            description = "Active Map - Track your favorite locations"
            vendor = "ActiveMap"
            
            linux {
                iconFile.set(project.file("icon.png"))
            }
            windows {
                iconFile.set(project.file("icon.ico"))
                menuGroup = "ActiveMap"
                upgradeUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
            }
            macOS {
                iconFile.set(project.file("icon.icns"))
                bundleID = "com.activemap.desktop"
            }
        }
    }
}
