plugins {
    id("java")
    id("maven-publish")
    id("java-gradle-plugin")
    id("com.gradleup.shadow")
}

description = "Tool to decompile Minecraft's server code and extract the source code"
version = getStrataWorkspaceVersion()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    mavenCentral()
    maven("https://nexus.covers1624.net/repository/maven-releases/")
}

dependencies {
    implementation("io.codechicken:DiffPatch:2.1.0.43")
    implementation("org.vineflower:vineflower:1.11.2")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("de.oliver.FancyAnalytics:logger:0.0.8")
    implementation("org.jetbrains:annotations:26.1.0")
}

gradlePlugin {
    plugins {
        register("com.origamimc.strata-workspace") {
            id = "com.origamimc.strata-workspace"
            version = getStrataWorkspaceVersion()
            description = "Gradle plugin for Origami"
            implementationClass = "com.origamimc.strata.plugin.StrataGradlePlugin"
        }
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "com.origamimc.strata.cli.Main"
        }
    }

    shadowJar {
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(25)
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}

fun getStrataWorkspaceVersion(): String {
    return file("VERSION").readText()
}
