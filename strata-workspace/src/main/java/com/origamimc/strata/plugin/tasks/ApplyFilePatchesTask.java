package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import com.origamimc.strata.utils.SleepUtils;
import com.origamimc.strata.workspace.WorkspaceService;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class ApplyFilePatchesTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> minecraftVersionProperty = getProject().getObjects().property(String.class);

    @Input
    private final Property<String> patchesDirProperty = getProject().getObjects().property(String.class);

    public ApplyFilePatchesTask() {
        setGroup("strata");
        setDescription("Apply per-file patches to the Minecraft source code");
        dependsOn("initStrata");
    }

    @TaskAction
    public void run() {
        Strata strata = strataProperty.get();
        String mcVersion = minecraftVersionProperty.get();
        PistonVersionDetails versionDetails = strata.getMojangService().getVersion(mcVersion);
        if (versionDetails == null) {
            return;
        }
        String patchesDir = patchesDirProperty.get();

        // Reset to sources commit
        strata.getWorkspaceService().gitResetHard(WorkspaceService.DECOMPILED_SOURCES_TAG);
        SleepUtils.sleep(1000);

        // Apply patches
        String originalDir = strata.getCacheDir().getAbsolutePath() + "/decompiled/" + versionDetails.id();
        strata.getPatcherService().applyFilePatches(
                originalDir,
                strata.getSourceDir().getAbsolutePath(),
                patchesDir+"/files",
                patchesDir+"/rejected-files"
        );
        SleepUtils.sleep(1000);

        // Commit patches
        strata.getWorkspaceService().gitCommit("Apply file patches");
        strata.getWorkspaceService().gitTag(WorkspaceService.FILE_PATCHES_TAG);
        SleepUtils.sleep(1000);
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
