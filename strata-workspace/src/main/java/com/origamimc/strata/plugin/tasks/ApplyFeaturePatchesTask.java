package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.utils.SleepUtils;
import com.origamimc.strata.workspace.WorkspaceService;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class ApplyFeaturePatchesTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> patchesDirProperty = getProject().getObjects().property(String.class);

    public ApplyFeaturePatchesTask() {
        setGroup("strata");
        setDescription("Apply feature patches to the Minecraft source code");
        dependsOn("initStrata");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        String patchesDir = patchesDirProperty.get();

        // Reset to file patches commit
        strata.getWorkspaceService().gitResetHard(WorkspaceService.FILE_PATCHES_TAG);
        SleepUtils.sleep(1000);

        // Apply patches
        strata.getPatcherService().applyFeaturePatches(patchesDir+"/features");
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getPatchesDirProperty() {
        return patchesDirProperty;
    }
}
