package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class DecompileMinecraftJarTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> minecraftVersionProperty = getProject().getObjects().property(String.class);


    public DecompileMinecraftJarTask() {
        setGroup("strata internal");
        setDescription("Decompiles the Minecraft server jar");
        dependsOn("downloadMinecraftJar");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        String mcVersion = minecraftVersionProperty.get();
        PistonVersionDetails versionDetails = strata.getMojangService().getVersion(mcVersion);
        if (versionDetails == null) {
            return;
        }

        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(versionDetails.id()),
                versionDetails.id()
        );
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getMinecraftVersionProperty() {
        return minecraftVersionProperty;
    }
}
