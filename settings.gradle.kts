pluginManagement {
    repositories {
        mavenLocal()
        maven(url = "https://maven.fancyspaces.net/origami/releases")
        maven(url = "https://repo.fancyinnovations.com/releases")
        gradlePluginPortal()
    }
}

rootProject.name = "strata"

include(":strata-workspace")
include(":test-env")
