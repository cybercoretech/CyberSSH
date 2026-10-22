plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "dev.cybercore.cyberssh"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "dev.cybercore.cyberssh"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
    buildFeatures { compose = true }
    lint { warningsAsErrors = true }
    packaging {
        resources.excludes += "META-INF/DEPENDENCIES"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.10.00"))
    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    implementation("org.apache.sshd:sshd-core:2.20.0")
    implementation("org.apache.sshd:sshd-sftp:2.20.0")
    implementation("com.termux.termux-app:terminal-view:0.118.0")
    implementation("com.termux.termux-app:terminal-emulator:0.118.0")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("com.google.guava:guava:33.3.1-android")
    implementation("androidx.concurrent:concurrent-futures:1.1.0")

    testImplementation("junit:junit:4.13.2")
}
