buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        //classpath(libs.bundles.plugins)
        classpath (libs.moko.resources.generator)
        classpath (libs.plugin.kotlin.serialization)
       classpath (libs.plugin.kotlin)
        classpath (libs.plugin.gver)
        classpath (libs.plugin.android)
        classpath (libs.sqldelight.plugin)
        classpath (libs.parcelize.gradle.plugin)
    }

}
plugins {
    //trick: for the same plugin versions in all sub-modules
    kotlin("jvm").version("1.9.0").apply(false)
    kotlin("android").version("1.9.0").apply(false)
    kotlin("multiplatform").version("1.9.0").apply(false)

    id("com.android.application").version("8.1.0").apply(false)
    id("com.android.library").version("8.1.0").apply(false)
    id("org.jetbrains.compose").version("1.5.0").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
