package com.origamimc.strata;

import com.origamimc.strata.decompiler.DecompilerService;
import com.origamimc.strata.extractor.ExtractorService;
import com.origamimc.strata.mojang.MojangService;
import com.origamimc.strata.patcher.PatcherService;
import com.origamimc.strata.workspace.WorkspaceService;
import com.google.gson.Gson;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;

import java.io.File;
import java.util.List;

public class Strata {

    public static Gson GSON = new Gson();

    private final ExtendedFancyLogger logger;
    private final File cacheDir;

    private final MojangService mojangService;
    private final ExtractorService extractorService;
    private final DecompilerService decompilerService;
    private final WorkspaceService workspaceService;
    private final PatcherService patcherService;

    public Strata(String cacheDirPath) {
        logger = new ExtendedFancyLogger(
                "Strata",
                LogLevel.INFO,
                List.of(new ConsoleAppender()),
                List.of()
        );

        cacheDir = new File(cacheDirPath);
        if (!cacheDir.exists()) {
            boolean created = cacheDir.mkdirs();
            if (created) {
                logger.info("Created cache directory at " + cacheDir.getAbsolutePath());
            } else {
                logger.warn("Failed to create cache directory at " + cacheDir.getAbsolutePath());
            }
        }

        mojangService = new MojangService(this);
        extractorService = new ExtractorService(this);
        decompilerService = new DecompilerService(this);
        workspaceService = new WorkspaceService(this);
        patcherService = new PatcherService(this);
    }

    public void init() {
        logger.info("Initializing Strata...");
    }

    public ExtendedFancyLogger getLogger() {
        return logger;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public MojangService getMojangService() {
        return mojangService;
    }

    public ExtractorService getExtractorService() {
        return extractorService;
    }

    public DecompilerService getDecompilerService() {
        return decompilerService;
    }

    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    public PatcherService getPatcherService() {
        return patcherService;
    }
}
