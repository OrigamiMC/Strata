package com.origamimc.strata.plugin;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

public class StrataExtension {

    private final Property<String> minecraftVersion;

    private final Property<String> cacheDir;

    private final Property<String> sourceDir;

    private final Property<String> patchesDir;

    private final Property<String> bootstrapJarPath;

    public StrataExtension(ObjectFactory objects) {
        minecraftVersion = objects.property(String.class).convention("latest-release");
        cacheDir = objects.property(String.class);
        sourceDir = objects.property(String.class);
        patchesDir = objects.property(String.class);
        bootstrapJarPath = objects.property(String.class);
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

    public Property<String> getPatchesDir() {
        return patchesDir;
    }

    public Property<String> getBootstrapJarPath() {
        return bootstrapJarPath;
    }
}
