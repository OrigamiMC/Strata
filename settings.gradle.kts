pluginManagement {
    repositories {
        mavenLocal()
        maven(url = "https://maven.fancyspaces.net/origami/releases")
        maven(url = "https://maven.fancyspaces.net/fancyinnovations/releases")
        gradlePluginPortal()
    }
}

rootProject.name = "strata"

include(":strata-workspace")
include(":test-env")
