package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class CreateServerJarPatchTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> minecraftVersionProperty = getProject().getObjects().property(String.class);

    @Input
    private final Property<String> patchedJarPathProperty = getProject().getObjects().property(String.class);

    public CreateServerJarPatchTask() {
        setGroup("strata");
        setDescription("Creates a bsdiff patch of the patched Minecraft server jar");
        dependsOn("initStrata", "shadowJar");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        String mcVersion = minecraftVersionProperty.get();
        PistonVersionDetails versionDetails = strata.getMojangService().getVersion(mcVersion);
        if (versionDetails == null) {
            return;
        }
        String patchedJarPath = patchedJarPathProperty.get();

        String originalJarPath = strata.getCacheDir().toPath().resolve("server-jars/server-" + versionDetails.id() + ".jar").toString();

        String patchPath = strata.getCacheDir().toPath().resolve("server-jar-patches/server-" + versionDetails.id() + ".patch").toString();

        strata.getPatcherService().createJarPatch(originalJarPath, patchedJarPath, patchPath);
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getMinecraftVersionProperty() {
        return minecraftVersionProperty;
    }

    public Property<String> getPatchedJarPathProperty() {
        return patchedJarPathProperty;
    }
}
