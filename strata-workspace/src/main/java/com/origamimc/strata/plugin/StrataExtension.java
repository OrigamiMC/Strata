package com.origamimc.strata.plugin;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class StrataExtension {

    private final Property<String> minecraftVersion;

    private final Property<String> cacheDir;

    private final Property<String> sourceDir;

    public StrataExtension(ObjectFactory objects) {
        minecraftVersion = objects.property(String.class).convention("latest-release");
        cacheDir = objects.property(String.class);
        sourceDir = objects.property(String.class);
    }

    public Property<String> getMinecraftVersion() {
        return minecraftVersion;
    }

    public Property<String> getCacheDir() {
        return cacheDir;
    }

    public Property<String> getSourceDir() {
        return sourceDir;
    }
}
