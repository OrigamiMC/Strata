package com.origamimc.strata.cli;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import com.origamimc.strata.utils.SleepUtils;

public class Main {

    /**
     * For mc-diff
     */
    static void main(String[] args) {
        String gitDir = "mc-diff/src";
        Strata strata = new Strata(() -> "mc-diff/strata-cache", () -> gitDir);
        strata.init();

        // Setup git repository
        // strata.getWorkspaceService().initGitDirectory("mc-diff");

        // Fetch version
        String version = "26.2-snapshot-3";

        PistonVersionDetails ver = strata.getMojangService().getVersion(version);
        strata.getMojangService().downloadServerBundle(ver);
        strata.getExtractorService().extractServerBundle(ver.id());

        // Decompile server jar
        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(ver.id()),
                ver.id()
        );
        strata.getWorkspaceService().copyDecompiledSources(ver.id());
        SleepUtils.sleep(1000);

        // Commit changes
        strata.getWorkspaceService().gitCommit("Update to " + version);
    }

}
