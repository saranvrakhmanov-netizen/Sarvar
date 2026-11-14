plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Версию можно передать при сборке: -PappVersionName=1.2.0 -PappVersionCode=5
val appVersionName: String = (project.findProperty("appVersionName") as String?) ?: "1.0.0"
val appVersionCode: Int = (project.findProperty("appVersionCode") as String?)?.toIntOrNull() ?: 1

android {
    namespace = "ru.himlab.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.himlab.app"
        minSdk = 26
        targetSdk = 34
        versionCode = appVersionCode
        versionName = appVersionName
    }

    // Постоянный семейный ключ подписи из репозитория: все APK подписаны одинаково,
    // поэтому новую версию можно ставить поверх старой без потери прогресса.
    signingConfigs {
        create("family") {
            storeFile = rootProject.file("keystore/himlab-family.keystore")
            storePassword = "himlab123"
            keyAlias = "himlab"
            keyPassword = "himlab123"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("family")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("family")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
}
