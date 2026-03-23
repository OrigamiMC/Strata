package com.origamimc.strata;

import com.google.gson.Gson;
import com.origamimc.strata.decompiler.DecompilerService;
import com.origamimc.strata.extractor.ExtractorService;
import com.origamimc.strata.mojang.MojangService;
import com.origamimc.strata.patcher.PatcherService;
import com.origamimc.strata.workspace.WorkspaceService;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

public class Strata {

    public static Gson GSON = new Gson();

    private final ExtendedFancyLogger logger;
    private final Supplier<String> cacheDirPath;
    private final Supplier<String> sourceDirPath;

    private File cacheDir;
    private File sourceDir;
    private MojangService mojangService;
    private ExtractorService extractorService;
    private DecompilerService decompilerService;
    private WorkspaceService workspaceService;
    private PatcherService patcherService;

    public Strata(Supplier<String> cacheDirPath, Supplier<String> sourceDirPath) {
        logger = new ExtendedFancyLogger(
                "Strata",
                LogLevel.INFO,
                List.of(new ConsoleAppender()),
                List.of()
        );
        this.cacheDirPath = cacheDirPath;
        this.sourceDirPath = sourceDirPath;
    }

    public void init() {
        logger.info("Initializing Strata...");

        cacheDir = new File(cacheDirPath.get());
        if (!cacheDir.exists()) {
            boolean created = cacheDir.mkdirs();
            if (created) {
                logger.info("Created cache directory at " + cacheDir.getAbsolutePath());
            } else {
                logger.warn("Failed to create cache directory at " + cacheDir.getAbsolutePath());
            }
        }

        sourceDir = new File(sourceDirPath.get());
        if (!sourceDir.exists()) {
            boolean created = sourceDir.mkdirs();
            if (created) {
                logger.info("Created source directory at " + sourceDir.getAbsolutePath());
            } else {
                logger.warn("Failed to create source directory at " + sourceDir.getAbsolutePath());
            }
        }

        mojangService = new MojangService(this);
        extractorService = new ExtractorService(this);
        decompilerService = new DecompilerService(this);
        workspaceService = new WorkspaceService(this);
        patcherService = new PatcherService(this);
    }

    public ExtendedFancyLogger getLogger() {
        return logger;
    }

    public File getCacheDir() {
        return cacheDir;
    }

    public File getSourceDir() {
        return sourceDir;
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
