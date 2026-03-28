package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import com.origamimc.strata.utils.SleepUtils;
import com.origamimc.strata.workspace.WorkspaceService;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class FixFilePatchesTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    public FixFilePatchesTask() {
        setGroup("strata");
        setDescription("Updates the file patches commit with local changes");
        dependsOn("initStrata");
    }

    @TaskAction
    public void run() {
        Strata strata = strataProperty.get();

        // Add changes to commit
        strata.getWorkspaceService().gitCommit("", true);
        SleepUtils.sleep(1000);

        // Update tag
        strata.getWorkspaceService().gitTag(WorkspaceService.FILE_PATCHES_TAG);
        SleepUtils.sleep(1000);
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }
}
