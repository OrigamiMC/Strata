package com.origamimc.strata.plugin;


import com.origamimc.strata.Strata;
import com.origamimc.strata.plugin.tasks.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.function.Supplier;

public class StrataGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        StrataExtension extension = project.getExtensions().create("strata", StrataExtension.class);

        Supplier<String> cacheDir = () -> extension.getCacheDir().isPresent() ?
                extension.getCacheDir().get() :
                project.getLayout().getBuildDirectory().dir("strata-cache").get().getAsFile().getAbsolutePath();

        Supplier<String> sourceDir = () -> extension.getSourceDir().isPresent() ?
                extension.getSourceDir().get() :
                project.getLayout().getProjectDirectory().dir("src/main/java").getAsFile().getAbsolutePath();

        Supplier<String> patchesDir = () -> extension.getPatchesDir().isPresent() ?
                extension.getPatchesDir().get() :
                project.getLayout().getProjectDirectory().dir("patches").getAsFile().getAbsolutePath();

        Strata strata = new Strata(cacheDir, sourceDir);

        // Register tasks
        project.getTasks().register("initStrata", InitTask.class, task -> {
            task.getStrataProperty().set(strata);
        });

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

        project.getTasks().register("applyFilePatches", ApplyFilePatchesTask.class, task -> {
            task.getStrataProperty().set(strata);
            task.getMinecraftVersionProperty().set(extension.getMinecraftVersion());
            task.getPatchesDirProperty().set(patchesDir.get());
        });

        project.getTasks().register("fixFilePatches", FixFilePatchesTask.class, task -> {
            task.getStrataProperty().set(strata);
        });

        project.getTasks().register("rebuildFilePatches", RebuildFilePatchesTask.class, task -> {
            task.getStrataProperty().set(strata);
            task.getMinecraftVersionProperty().set(extension.getMinecraftVersion());
            task.getPatchesDirProperty().set(patchesDir.get());
        });

        project.getTasks().register("rebuildFeaturePatches", RebuildFeaturePatchesTask.class, task -> {
            task.getStrataProperty().set(strata);
            task.getPatchesDirProperty().set(patchesDir.get());
        });

        project.getTasks().register("applyFeaturePatches", ApplyFeaturePatchesTask.class, task -> {
            task.getStrataProperty().set(strata);
            task.getPatchesDirProperty().set(patchesDir.get());
        });

        // Add Minecraft libraries repository
        project.getRepositories().maven(repo -> {
            repo.setName("Minecraft Libraries");
            repo.setUrl("https://libraries.minecraft.net/");
        });
    }

}
