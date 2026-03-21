package com.origamimc.strata.plugin;


import com.origamimc.strata.plugin.tasks.DownloadMinecraftJarTask;
import com.origamimc.strata.plugin.tasks.SetupTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class StrataGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        StrataExtension extension = project.getExtensions().create("strata", StrataExtension.class);

        project.getTasks().register("setupStrata", SetupTask.class);
        project.getTasks().register("downloadMinecraftJar", DownloadMinecraftJarTask.class, task -> {
            task.getMinecraftVersion().set(extension.getMinecraftVersion());

            if (extension.getCacheDir().isPresent()) {
                task.getCacheDir().set(extension.getCacheDir());
            } else {
                String strataCacheDir = project.getLayout().getBuildDirectory().dir("strata-cache").get().getAsFile().getAbsolutePath();
                task.getCacheDir().set(strataCacheDir);
            }
        });
    }

}
