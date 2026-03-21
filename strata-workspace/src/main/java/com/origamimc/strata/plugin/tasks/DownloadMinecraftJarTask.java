package com.origamimc.strata.plugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.nio.file.Path;

public class DownloadMinecraftJarTask extends DefaultTask {

    @Input
    private final Property<String> minecraftVersion = getProject().getObjects()
            .property(String.class)
            .convention("latest");

    @Input
    private final Property<String> cacheDir = getProject().getObjects().property(String.class);

    public DownloadMinecraftJarTask() {
        setGroup("strata internal");
        setDescription("Downloads the Minecraft server jar");
    }

    @TaskAction
    public void downloadSources() {
        System.out.println("Downloading Minecraft server jar for version " + minecraftVersion.get());
        System.out.println("Using cache directory: " + Path.of(cacheDir.get()).toAbsolutePath());
    }

    public Property<String> getMinecraftVersion() {
        return minecraftVersion;
    }

    public Property<String> getCacheDir() {
        return cacheDir;
    }
}
