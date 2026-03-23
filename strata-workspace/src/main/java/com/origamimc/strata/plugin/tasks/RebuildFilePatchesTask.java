package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import com.origamimc.strata.utils.SleepUtils;
import com.origamimc.strata.workspace.WorkspaceService;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class RebuildFilePatchesTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> minecraftVersionProperty = getProject().getObjects().property(String.class);

    @Input
    private final Property<String> patchesDirProperty = getProject().getObjects().property(String.class);

    public RebuildFilePatchesTask() {
        setGroup("strata");
        setDescription("Rebuilds the file patches based on the current source code and the decompiled sources");
        dependsOn("initStrata");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        String mcVersion = minecraftVersionProperty.get();
        PistonVersionDetails versionDetails = strata.getMojangService().getVersion(mcVersion);
        if (versionDetails == null) {
            return;
        }
        String patchesDir = patchesDirProperty.get();

        // Reset to file patches commit
        strata.getWorkspaceService().gitResetHard(WorkspaceService.FILE_PATCHES_TAG);
        SleepUtils.sleep(1000);

        // Rebuild patches
        String originalDir = strata.getCacheDir().getAbsolutePath() + "/decompiled/" + versionDetails.id();
        strata.getPatcherService().rebuildFilePatches(
                originalDir,
                strata.getSourceDir().getAbsolutePath(),
                patchesDir+"/files"
        );
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getMinecraftVersionProperty() {
        return minecraftVersionProperty;
    }

    public Property<String> getPatchesDirProperty() {
        return patchesDirProperty;
    }
}
