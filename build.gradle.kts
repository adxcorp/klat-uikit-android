// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    alias(libs.plugins.androidLibrary) apply false
//    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
//    alias(libs.plugins.kotlinSerialization) apply false
//    id("maven-publish")
    id("com.android.library") version "7.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.7.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.0" apply false
    id("maven-publish")
}