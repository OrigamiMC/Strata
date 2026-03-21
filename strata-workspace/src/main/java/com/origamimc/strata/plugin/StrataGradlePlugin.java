package com.origamimc.strata.plugin;


import com.origamimc.strata.Strata;
import com.origamimc.strata.plugin.tasks.DecompileMinecraftJarTask;
import com.origamimc.strata.plugin.tasks.DownloadMinecraftJarTask;
import com.origamimc.strata.plugin.tasks.SetupGitRepoTask;
import com.origamimc.strata.plugin.tasks.SetupTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class StrataGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        StrataExtension extension = project.getExtensions().create("strata", StrataExtension.class);

        String cacheDir = extension.getCacheDir().isPresent() ?
                extension.getCacheDir().get() :
                project.getLayout().getBuildDirectory().dir("strata-cache").get().getAsFile().getAbsolutePath();

        String sourceDir = extension.getSourceDir().isPresent() ?
                extension.getSourceDir().get() :
                project.getLayout().getProjectDirectory().dir("src/main/java").getAsFile().getAbsolutePath();

        Strata strata = new Strata(cacheDir, sourceDir);
        strata.init();

        // register tasks
        project.getTasks().register("setupStrata", SetupTask.class);

        project.getTasks().register("downloadMinecraftJar", DownloadMinecraftJarTask.class, task -> {
            task.getStrataProperty().set(strata);
            task.getMinecraftVersionProperty().set(extension.getMinecraftVersion());
        });

        project.getTasks().register("decompileMinecraftJar", DecompileMinecraftJarTask.class, task -> {
            task.getStrataProperty().set(strata);
            task.getMinecraftVersionProperty().set(extension.getMinecraftVersion());
        });

        project.getTasks().register("setupGitRepo", SetupGitRepoTask.class, task -> {
            task.getStrataProperty().set(strata);
            task.getMinecraftVersionProperty().set(extension.getMinecraftVersion());
        });
    }

}
