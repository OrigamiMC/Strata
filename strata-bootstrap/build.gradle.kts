plugins {
    id("java")
    id("com.gradleup.shadow")
}

description = "Bootstrap application for Minecraft server"
version = getStrataWorkspaceVersion()

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

dependencies {
    implementation(project(":strata-workspace"))

    implementation("io.sigpipe:jbsdiff:1.0")
    implementation("de.oliver.FancyAnalytics:logger:0.0.10")
    implementation("org.jetbrains:annotations:26.1.0")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "com.origamimc.strata.boostrap.Main"
        }
    }

    shadowJar {
        archiveFileName.set("strata-bootstrap.jar")
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
    return file("../strata-workspace/VERSION").readText()
}
