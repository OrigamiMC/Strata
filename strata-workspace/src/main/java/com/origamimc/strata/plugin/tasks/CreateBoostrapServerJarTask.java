package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class CreateBoostrapServerJarTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> minecraftVersionProperty = getProject().getObjects().property(String.class);


    public CreateBoostrapServerJarTask() {
        setGroup("strata");
        setDescription("Builds a boostrap jar (including patch file and other metadata)");
        dependsOn("initStrata", "createServerJarPatch");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        String mcVersion = minecraftVersionProperty.get();
        PistonVersionDetails versionDetails = strata.getMojangService().getVersion(mcVersion);
        if (versionDetails == null) {
            return;
        }

        String patchPath = strata.getCacheDir().toPath().resolve("server-jar-patches/server-" + versionDetails.id() + ".patch").toString();

        // TODO copy "META-INF/strata-bootstap.jar" to cache dir

        // TODO inject mc version into bootstrap jar at "META-INF/version.txt"

        // TODO copy patch into bootstrap jar at "META-INF/server-<version>.patch"

    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getMinecraftVersionProperty() {
        return minecraftVersionProperty;
    }

}
