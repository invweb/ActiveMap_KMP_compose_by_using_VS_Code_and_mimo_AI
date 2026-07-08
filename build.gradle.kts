plugins {
    kotlin("multiplatform") version "2.1.10" apply false
    kotlin("plugin.serialization") version "2.1.10" apply false
    id("com.android.application") version "8.9.1" apply false
    id("com.android.library") version "8.9.1" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" apply false
}

allprojects {
    group = "com.activemap"
    version = "1.0.0"
    
    configurations.all {
        exclude(group = "com.intellij", module = "annotations")
    }
}
