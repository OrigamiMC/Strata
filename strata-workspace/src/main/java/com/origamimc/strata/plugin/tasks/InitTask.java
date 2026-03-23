package com.origamimc.strata.plugin.tasks;

import com.origamimc.strata.Strata;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

public class InitTask extends DefaultTask {

    @Input
    private final Property<Strata> strataProperty = getProject().getObjects().property(Strata.class);

    public InitTask() {
        setGroup("strata internal");
        setDescription("Sets up strata");
    }

    @TaskAction
    public void downloadSources() {
        Strata strata = strataProperty.get();
        if (strata.getMojangService() == null) {
            strata.init();
        }
    }

    public Property<Strata> getStrataProperty() {
        return strataProperty;
    }

}
