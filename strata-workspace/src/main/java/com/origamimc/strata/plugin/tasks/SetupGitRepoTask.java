package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import com.origamimc.strata.utils.SleepUtils;
import com.origamimc.strata.workspace.WorkspaceService;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class SetupGitRepoTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    @Input
    private final Property<String> minecraftVersionProperty = getProject().getObjects().property(String.class);


    public SetupGitRepoTask() {
        setGroup("strata internal");
        setDescription("Sets up the internal git repository for the Minecraft sources");
        dependsOn("decompileMinecraftJar");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        String mcVersion = minecraftVersionProperty.get();
        PistonVersionDetails versionDetails = strata.getMojangService().getVersion(mcVersion);
        if (versionDetails == null) {
            return;
        }

        // Init repo
        strata.getWorkspaceService().initGitDirectory();
        SleepUtils.sleep(1000);

        // Add decompiled sources and resources
        strata.getWorkspaceService().copyDecompiledSources(versionDetails.id());
        strata.getWorkspaceService().copyDataAndAssets(versionDetails.id());
        strata.getWorkspaceService().gitCommit("Add decompiled sources");
        strata.getWorkspaceService().gitTag(WorkspaceService.DECOMPILED_SOURCES_TAG);
        SleepUtils.sleep(1000);
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

    public Property<String> getMinecraftVersionProperty() {
        return minecraftVersionProperty;
    }
}
