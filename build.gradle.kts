plugins {
    id("com.gradleup.shadow") version "9.3.1" apply false
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://maven.fancyspaces.net/origami/releases")
        maven(url = "https://maven.fancyspaces.net/fancyinnovations/releases")
        maven(url = "https://jitpack.io")
    }
}
