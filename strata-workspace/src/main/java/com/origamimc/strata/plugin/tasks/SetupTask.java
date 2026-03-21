package com.origamimc.strata.plugin.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class SetupTask extends DefaultTask {

    public SetupTask() {
        setGroup("strata");
        setDescription("Sets up the strata environment");
        dependsOn("decompileMinecraftJar");
    }

    @TaskAction
    public void runStrata() {
        System.out.println("Running Strata code generation...");
    }

}
