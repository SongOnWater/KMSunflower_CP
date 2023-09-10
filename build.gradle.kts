buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        //classpath(libs.bundles.plugins)
        classpath (libs.plugin.android)
        classpath (libs.plugin.gver)
        classpath (libs.plugin.kotlin)
        classpath (libs.plugin.kotlin.serialization)
        classpath (libs.moko.resources.generator)
        classpath (libs.parcelize.gradle.plugin)
       // classpath (libs.sqldelight.plugin)
       classpath (libs.cash.sqldelight.plugin)

    }

}
plugins {
    //trick: for the same plugin versions in all sub-modules
    val kotlinVersion = "1.9.10"

    kotlin("jvm").version(kotlinVersion).apply(false)
    kotlin("android").version(kotlinVersion).apply(false)
    kotlin("multiplatform").version(kotlinVersion).apply(false)

    id("com.android.application").version("8.1.0").apply(false)
    id("com.android.library").version("8.1.0").apply(false)
    id("org.jetbrains.compose").version("1.5.1").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
