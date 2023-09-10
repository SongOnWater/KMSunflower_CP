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
        kotlinCompilerExtensionVersion = "1.5.3"
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
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.cash.paging.compose.common)
    implementation(libs.moko.mvvm.core)
    implementation(libs.decompose)
    implementation(libs.decompose.router)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.accompanist.systemuicontroller)
    //implementation(libs.moko.mvvm.compose)
}