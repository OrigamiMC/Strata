package com.origamimc.strata.cli;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import com.origamimc.strata.workspace.WorkspaceService;

public class Main {

    /**
     *  For minecraft-source
     */
    public static void main(String[] args) {
        String cacheDir = "tools/strata/strata-cache";
        String sourceDir = "tools/strata/minecraft-source/src/main";
        Strata strata = new Strata(cacheDir, sourceDir);
        strata.init();

        // Get the latest snapshot version and download it
        PistonVersionDetails latest = strata.getMojangService().getLatestSnapshot();
        strata.getMojangService().downloadServerBundle(latest);
        strata.getExtractorService().extractServerBundle(latest.id());

        // Decompile
        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(latest.id()),
                latest.id()
        );

        // Setup git repo
        strata.getWorkspaceService().initGitDirectory();
        sleep(1000);

        // Add decompiled sources and resources
        strata.getWorkspaceService().copyDecompiledSources(latest.id());
        strata.getWorkspaceService().copyDataAndAssets(latest.id());
        strata.getWorkspaceService().gitCommit("Add decompiled sources");
        strata.getWorkspaceService().gitTag(WorkspaceService.DECOMPILED_SOURCES_TAG);
        sleep(1000);

        // Apply patches
        String patchesDir = "tools/strata/minecraft-source/patches";
        strata.getPatcherService().applyFilePatches(cacheDir+"/decompiled/"+latest.id(), sourceDir, patchesDir+"/files", patchesDir+"/rejected-files");
        sleep(1000);
        strata.getWorkspaceService().gitCommit("Apply file patches");
        strata.getWorkspaceService().gitTag(WorkspaceService.FILE_PATCHES_TAG);
        sleep(1000);

        strata.getLogger().info("Done with setting up workspace for version " + latest.id());

        // Rebuild patches
        // TODO refactor to different task
        // strata.getPatcherService().rebuildFilePatches(cacheDir+"/decompiled/"+latest.id(), gitDir, patchesDir+"/files");
    }

    /**
     * For minecraft-diff
     */
    public static void main2(String[] args) {
        String gitDir = "tools/strata/minecraft-diff/src";
        Strata strata = new Strata("tools/strata/strata-cache", gitDir);
        strata.init();

        String version = "26.1-pre-3";

        PistonVersionDetails ver = strata.getMojangService().getVersion(version);
        strata.getMojangService().downloadServerBundle(ver);
        strata.getExtractorService().extractServerBundle(ver.id());
        strata.getDecompilerService().decompile(
                strata.getExtractorService().getServerJarPath(ver.id()),
                ver.id()
        );


        strata.getWorkspaceService().copyDecompiledSources(ver.id());

        sleep(1000);

        strata.getWorkspaceService().gitCommit("Update to " + version);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

}
