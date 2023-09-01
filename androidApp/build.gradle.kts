plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.reverse.kmsunflower.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.reverse.kmsunflower.android"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
//tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
//    kotlinOptions {
//        freeCompilerArgs = listOf("-Xjvm-default=all")
//    }
//}

dependencies {
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.ui:ui-tooling:1.5.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.compose.foundation:foundation:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(libs.cash.paging.compose.common)
    implementation(libs.moko.mvvm.core)
    implementation(libs.decompose)
    implementation(libs.decompose.router)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.accompanist.themeadapter.material)
    //implementation(libs.moko.mvvm.compose)
}