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
    maven(url = "https://nexus.covers1624.net/repository/maven-releases/")
}

dependencies {
    implementation("io.codechicken:DiffPatch:2.1.0.43")
    implementation("io.sigpipe:jbsdiff:1.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("de.oliver.FancyAnalytics:logger:0.0.10")
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
    publishing {
        repositories {
            maven {
                name = "fancyspacesReleases"
                url = uri("https://maven.fancyspaces.net/origami/releases")

                credentials(HttpHeaderCredentials::class) {
                    name = "Authorization"
                    value = "ApiKey " + providers
                        .gradleProperty("fancyspacesApiKey")
                        .orElse(
                            providers
                                .environmentVariable("FANCYSPACES_API_KEY")
                                .orElse("")
                        )
                        .get()
                }

                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = getStrataWorkspaceVersion()
                from(project.components["java"])
            }
        }
    }

    jar {
        manifest {
            attributes["Main-Class"] = "com.origamimc.strata.cli.Main"
        }
    }

    shadowJar {
        archiveClassifier.set("")

        dependsOn(":strata-bootstrap:shadowJar")
        from("../strata-bootstrap/build/libs/strata-bootstrap.jar") {
            into("META-INF/strata-bootstrap")
        }
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
