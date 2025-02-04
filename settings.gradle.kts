import org.gradle.internal.impldep.com.google.gson.internal.bind.TypeAdapters.URI
import java.net.URI

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI("https://jitpack.io") }
    }
}
rootProject.name = "klat-uikit-android"
include(":app")

include(":talkplus")
project(":talkplus").projectDir = file("/Users/neptune/AndroidStudioProjects/talkplus-android/app")
 