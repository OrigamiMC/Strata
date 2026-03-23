package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class RebuildFeaturePatchesTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> patchesDirProperty = getProject().getObjects().property(String.class);

    public RebuildFeaturePatchesTask() {
        setGroup("strata");
        setDescription("Rebuilds the feature patches based on the commits after file patches");
        dependsOn("initStrata");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        String patchesDir = patchesDirProperty.get();

        strata.getPatcherService().rebuildFeaturePatches(patchesDir+"/features");
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getPatchesDirProperty() {
        return patchesDirProperty;
    }
}
