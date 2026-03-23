plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow")
    id("com.origamimc.strata-workspace") version "1.0.1"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

dependencies {
    implementation(fileTree("../strata-cache/server-libraries/libraries-26.1-pre-3") {
        include("**/*.jar")
    })

    implementation("org.jetbrains:annotations:24.0.1")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.checkerframework:checker-qual:3.49.0")
}

strata {
    minecraftVersion.set("26.1-rc-2")
//    cacheDir.set(layout.buildDirectory.dir("strata-cache").get().asFile.absolutePath)
//    sourceDir.set(file("src/main/java").absolutePath)
//    patchesDir.set(file("patches").absolutePath)
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "net.minecraft.server.Main"
        }
    }

    shadowJar {
        archiveFileName.set("minecraft-server.jar")
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(25)
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000"))
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
