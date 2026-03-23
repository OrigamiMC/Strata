# Strata

Gradle plugin for the Origami Minecraft fork.

Features:
- Fetch Minecraft version manifests from the official Mojang API
- Download Minecraft server jar and libraries
- Decompile Minecraft server jar using VineFlower
- Apply patches to the decompiled source code

## Usage

Add this repo to the `settings.gradle.kts` file:

```kotlin
pluginManagement {
    repositories {
        maven(url = "https://maven.fancyspaces.net/origami/releases")
        maven(url = "https://maven.fancyspaces.net/fancyinnovations/releases")
        gradlePluginPortal()
    }
}
```

Then apply the plugin in your `build.gradle.kts` file:

```kotlin
plugins {
    id("com.origamimc.strata-workspace") version "1.0.0"
}
```

You can configure strata in your `build.gradle.kts` file:

```kotlin
strata {
    minecraftVersion.set("26.1-rc-2")
    
    // optional
    // cacheDir.set(layout.buildDirectory.dir("strata-cache").get().asFile.absolutePath)
    // sourceDir.set(file("src/main/java").absolutePath)
    // patchesDir.set(file("patches").absolutePath)
}
```