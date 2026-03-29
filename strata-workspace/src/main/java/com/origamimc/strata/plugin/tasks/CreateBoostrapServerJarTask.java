package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import com.origamimc.strata.utils.ResourceUtils;
import com.origamimc.strata.utils.ZipUtils;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CreateBoostrapServerJarTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> minecraftVersionProperty = getProject().getObjects().property(String.class);

    @Input
    private final Property<String> bootstrapJarPathProperty = getProject().getObjects().property(String.class);

    public CreateBoostrapServerJarTask() {
        setGroup("strata");
        setDescription("Builds a boostrap jar (including patch file and other metadata)");
        dependsOn("initStrata", "createServerJarPatch");
    }

    @TaskAction
    public void run() {
        Strata strata = strataProperty.get();
        String mcVersion = minecraftVersionProperty.get();
        PistonVersionDetails versionDetails = strata.getMojangService().getVersion(mcVersion);
        if (versionDetails == null) {
            return;
        }

        // Copy bootstrap jar from resources to cache
        Path boostrapJarPath = Path.of(bootstrapJarPathProperty.get());
        if (!Files.exists(boostrapJarPath.getParent())) {
            boostrapJarPath.getParent().toFile().mkdirs();
        }

        byte[] boostrapJarData = ResourceUtils.readResourceToBytes("META-INF/strata-bootstrap.jar");
        try {
            Files.write(boostrapJarPath, boostrapJarData);
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to write bootstrap file to cache",
                    ThrowableProperty.of(e)
            );
        }

        // Inject version to bootstrap jar
        try {
            ZipUtils.addFileToZip(versionDetails.id(), "version.txt", boostrapJarPath.toString());
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to inject version.txt to bootstrap jar",
                    ThrowableProperty.of(e)
            );
        }

        // Copy patch into bootstrap jar
        String patchPath = strata.getCacheDir().toPath().resolve("server-jar-patches/server-" + versionDetails.id() + ".patch").toString();
        try {
            ZipUtils.injectToZip(patchPath, boostrapJarPath.toString());
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to inject patch file to bootstrap jar",
                    ThrowableProperty.of(e)
            );
        }
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getMinecraftVersionProperty() {
        return minecraftVersionProperty;
    }

    public Property<String> getBootstrapJarPathProperty() {
        return bootstrapJarPathProperty;
    }
}
