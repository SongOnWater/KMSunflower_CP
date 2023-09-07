@file:Suppress("OPT_IN_IS_NOT_ENABLED")

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization")
    id("com.android.library")


    id("com.squareup.sqldelight")
    id("dev.icerock.mobile.multiplatform-resources")
    id("org.jetbrains.compose")
    id("kotlin-parcelize")
    id("com.arkivanov.parcelize.darwin")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
   // targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    ios()
    //iosArm64()
    iosArm64 {
        binaries.executable()

        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xallocator=custom")
            }
        }
    }
    iosSimulatorArm64{
        binaries.executable()

        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.add("-Xallocator=custom")
            }
        }
    }


    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            //export(libs.moko.resources)
            //export(libs.moko.graphics)

            //export(libs.moko.mvvm.core)
            //export(libs.moko.mvvm.livedata)
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"

    }

    @kotlin.Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.napier)
                implementation(libs.ktor.core)
                implementation(libs.ktor.logging)
                implementation(libs.ktor.client.contentnegotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.cash.paging.common)
                //implementation(libs.cash.paging.runtime)
                implementation(libs.cash.paging.compose.common)
                implementation(libs.cash.paging.runtime.composeui)



                implementation(libs.coroutine.worker)
                implementation(libs.napier.log)

                api(libs.moko.resources)
                implementation(libs.moko.resources.compose)
                api(libs.moko.mvvm.core)
                //implementation(libs.moko.mvvm.compose)
                api(libs.moko.mvvm.livedata)
                implementation(libs.moko.mvvm.livedata.compose)

                implementation(libs.decompose)
                implementation(libs.decompose.compose.multiplatform)
                implementation(libs.decompose.router)

                implementation(libs.kermit)
                implementation(libs.insetsx)
                implementation(libs.richeditor.compose)
                api(libs.image.loader)
                implementation(libs.image.loader.extension.moko.resources)

            }
        }


        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.sqldelight.android.driver)
                //implementation(libs.androidx.paging)
                implementation(libs.cash.paging.compose.common.android)
                implementation(libs.cash.paging.runtime.composeui.android)
            }
        }

        val iosMain by getting {
            dependencies {
                implementation(libs.ktor.client.ios)
                implementation(libs.sqldelight.native.driver)
                implementation(libs.cash.paging.runtime.uikit)
            }
        }

        val iosArm64Main by getting{
            dependsOn(iosMain)
        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }



        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }

}

android {
    namespace = "com.reverse.kmsunflower"
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    buildFeatures {
        //dataBinding = true
        buildConfig = true
    }
    defaultConfig {
        minSdk = 26
        buildConfigField("String", "UNSPLASH_ACCESS_KEY", "\"" + getUnsplashAccess() + "\"")
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.reverse.kmsunflower"
    multiplatformResourcesClassName = "MR"
}

fun getUnsplashAccess(): String? {
    return project.findProperty("unsplash_access_key") as? String
}
sqldelight {
    database("Database") {
        packageName = "com.reverse.kmsunflower.data"
    }
}