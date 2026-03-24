package com.origamimc.strata.patcher;

import com.origamimc.strata.Strata;
import com.origamimc.strata.utils.SleepUtils;
import com.origamimc.strata.workspace.WorkspaceService;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import io.codechicken.diffpatch.cli.CliOperation;
import io.codechicken.diffpatch.cli.DiffOperation;
import io.codechicken.diffpatch.cli.PatchOperation;
import io.codechicken.diffpatch.match.FuzzyLineMatcher;
import io.codechicken.diffpatch.util.Input;
import io.codechicken.diffpatch.util.LogLevel;
import io.codechicken.diffpatch.util.Output;
import io.codechicken.diffpatch.util.PatchMode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PatcherService {

    private final Strata strata;

    public PatcherService(Strata strata) {
        this.strata = strata;
    }

    public void rebuildFilePatches(String originalSourcePath, String patchedSourcePath, String patchesPath) {
        // Delete existing patches
        try {
            Path patchesDir = Path.of(patchesPath);
            if (patchesDir.toFile().exists()) {
                Files.walk(patchesDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to clear existing patches",
                    ThrowableProperty.of(e),
                    StringProperty.of("patchesPath", patchesPath)
            );
            return;
        }

        try {
            CliOperation.Result<DiffOperation.DiffSummary> result = DiffOperation.builder()
                    .baseInput(Input.MultiInput.folder(Path.of(originalSourcePath)))
                    .changedInput(Input.MultiInput.folder(Path.of(patchedSourcePath)))
                    .patchesOutput(Output.MultiOutput.folder(Path.of(patchesPath)))
                    .autoHeader(true)
                    .ignorePrefix(".git")
                    .ignorePrefix("META-INF")
                    .ignorePrefix("data/")
                    .ignorePrefix("assets/")
                    .ignorePrefix("version.json")
                    .ignorePrefix("flightrecorder-config.jfc")
                    .context(3)
                    .logTo(s -> strata.getLogger().error(s))
                    .level(LogLevel.ERROR)
                    .summary(false)
                    .build()
                    .operate();

            strata.getLogger().info(
                    "Finished rebuilding file patches",
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to rebuild file patches",
                    ThrowableProperty.of(e),
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        }
    }

    public void applyFilePatches(String originalSourcePath, String patchedSourcePath, String patchesPath, String rejectsPath) {
        Path cache = strata.getCacheDir().toPath().resolve("source-with-patches");

        // Clear cache directory
        if (cache.toFile().exists()) {
            try {
                Files.walk(cache)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                strata.getLogger().error(
                        "Failed to clear patch application cache",
                        ThrowableProperty.of(e),
                        StringProperty.of("cachePath", cache.toString())
                );
                return;
            }
        }

        SleepUtils.sleep(1000);

        // Apply patches to original source, output to cache
        try {
            CliOperation.Result<PatchOperation.PatchesSummary> result = PatchOperation.builder()
                    .baseInput(Input.MultiInput.folder(Path.of(originalSourcePath)))
                    .patchesInput(Input.MultiInput.folder(Path.of(patchesPath)))
                    .patchedOutput(Output.MultiOutput.folder(cache))
                    .logTo(s -> strata.getLogger().error(s))
                    .level(LogLevel.ERROR)
                    .rejectsOutput(Output.MultiOutput.folder(Path.of(rejectsPath)))
                    .mode(PatchMode.OFFSET)
                    .minFuzz(FuzzyLineMatcher.DEFAULT_MIN_MATCH_SCORE)
                    .ignorePrefix(".git")
                    .ignorePrefix("META-INF")
                    .ignorePrefix("data/")
                    .ignorePrefix("assets/")
                    .ignorePrefix("version.json")
                    .ignorePrefix("flightrecorder-config.jfc")
                    .build()
                    .operate();

            strata.getLogger().info(
                    "Finished applying file patches",
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to apply file patches",
                    ThrowableProperty.of(e),
                    StringProperty.of("originalSourcePath", originalSourcePath),
                    StringProperty.of("patchedSourcePath", patchedSourcePath),
                    StringProperty.of("patchesPath", patchesPath)
            );
            return;
        }

        SleepUtils.sleep(1000);

        // clear patchedSourcePath
        try {
            Path patchedSourceDir = Path.of(patchedSourcePath);
            if (patchedSourceDir.toFile().exists()) {
                Files.walk(patchedSourceDir)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .filter(file -> !file.toPath().toString().contains(".git")) // don't delete .git directory
                        .forEach(File::delete);
            }
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to clear patched source directory",
                    ThrowableProperty.of(e),
                    StringProperty.of("patchedSourcePath", patchedSourcePath)
            );
            return;
        }

        SleepUtils.sleep(1000);

        // copy patched files to patchedSourcePath
        try {
            Files.walk(cache)
                    .filter(Files::isRegularFile)
                    .forEach(patchedFile -> {
                        Path relativePath = cache.relativize(patchedFile);
                        Path targetPath = Path.of(patchedSourcePath).resolve(relativePath);
                        try {
                            Files.createDirectories(targetPath.getParent());
                            Files.copy(patchedFile, targetPath);
                        } catch (IOException e) {
                            strata.getLogger().error(
                                    "Failed to copy patched file",
                                    ThrowableProperty.of(e),
                                    StringProperty.of("patchedFile", patchedFile.toString()),
                                    StringProperty.of("targetPath", targetPath.toString())
                            );
                        }
                    });
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to copy patched files",
                    ThrowableProperty.of(e),
                    StringProperty.of("cachePath", cache.toString()),
                    StringProperty.of("patchedSourcePath", patchedSourcePath)
            );
        }
    }

    public void rebuildFeaturePatches(String patchesPath) {
        // Clear patches directory
        if (new File(patchesPath).exists()) {
            try {
                Files.walk(Path.of(patchesPath))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                strata.getLogger().error(
                        "Failed to clear patches dir",
                        ThrowableProperty.of(e),
                        StringProperty.of("patchesPath", patchesPath)
                );
                return;
            }
        }

        String gitDir = strata.getSourceDir().getAbsolutePath();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "git",
                    "format-patch",
                    WorkspaceService.FILE_PATCHES_TAG + "..HEAD",
                    "--no-signature",
                    "--output-directory=" + patchesPath
            );
            processBuilder.directory(new File(gitDir));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                strata.getLogger().info("Finished rebuilding feature patches");
            } else {
                strata.getLogger().error("Failed to rebuild feature patches, git process exited with code " + exitCode);
            }
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to rebuild feature patches",
                    ThrowableProperty.of(e)
            );
        }
    }

    public void applyFeaturePatches(String patchesPath) {
        String gitDir = strata.getSourceDir().getAbsolutePath();

        File patchesDir = new File(patchesPath);
        File[] patchFiles = patchesDir.listFiles((dir, name) -> name.endsWith(".patch"));

        if (patchFiles == null || patchFiles.length == 0) {
            strata.getLogger().info("No feature patches to apply");
            return;
        }

        Arrays.sort(patchFiles); // ensure patches are applied in order

        try {
            List<String> command = new ArrayList<>();
            command.add("git");
            command.add("am");
            command.add("--3way");
            for (File patchFile : patchFiles) {
                command.add(patchFile.getAbsolutePath());
            }

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(gitDir));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                strata.getLogger().info("Finished applying feature patches");
            } else {
                strata.getLogger().error("Failed to apply feature patches, git process exited with code " + exitCode);
            }
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to apply feature patches",
                    ThrowableProperty.of(e)
            );
        }
    }


}
