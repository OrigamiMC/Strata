pluginManagement {
    repositories {
        mavenLocal()
        maven(url = "https://maven.fancyspaces.net/origami/releases")
        gradlePluginPortal()
    }
}

rootProject.name = "strata"

include(":strata-workspace")
