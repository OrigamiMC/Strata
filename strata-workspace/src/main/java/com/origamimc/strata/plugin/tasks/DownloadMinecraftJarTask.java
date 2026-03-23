package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class DownloadMinecraftJarTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> minecraftVersionProperty = getProject().getObjects()
            .property(String.class)
            .convention("latest-release");

    public DownloadMinecraftJarTask() {
        setGroup("strata internal");
        setDescription("Downloads the Minecraft server jar and it's libraries");
        dependsOn("initStrata");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        String mcVersion = minecraftVersionProperty.get();

        // fetch version manifest from Mojang api
        PistonVersionDetails latest = strata.getMojangService().getVersion(mcVersion);
        if (latest == null) {
            return;
        }

        // download jar from Mojang api
        if (!strata.getMojangService().downloadServerBundle(latest)) {
            return;
        }

        // extract the server jar and libraries from jar
        if (!strata.getExtractorService().extractServerBundle(latest.id())) {
            return;
        }
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getMinecraftVersionProperty() {
        return minecraftVersionProperty;
    }
}
