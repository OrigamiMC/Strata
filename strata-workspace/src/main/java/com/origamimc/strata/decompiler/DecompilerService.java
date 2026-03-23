package com.origamimc.strata.decompiler;

import com.origamimc.strata.Strata;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class DecompilerService {

    private static final String VINEFLOWER_DOWNLOAD_URL = "https://github.com/Vineflower/vineflower/releases/download/1.11.2/vineflower-1.11.2.jar";
    private static final String VINEFLOWER_FILE_NAME = "vineflower-1.11.2.jar";

    private final Strata strata;

    public DecompilerService(Strata strata) {
        this.strata = strata;
    }

    public void decompile(String inputJarPath, String versionID) {
        File inputFile = new File(inputJarPath);
        if (!inputFile.exists()) {
            strata.getLogger().error("Input JAR file does not exist: " + inputJarPath);
            return;
        }

        Path cacheDir = strata.getCacheDir().toPath();
        String outputDirPath = cacheDir.resolve("decompiled").resolve(versionID).toString();

        File outputDir = new File(outputDirPath);
        if (outputDir.exists()) {
            strata.getLogger().warn("Output directory already exists. Skipping decompilation.");
            return;
        }

        downloadVineflower();

        strata.getLogger().info("Starting to decompile server jar ...");

        try {
            String vineflowerPath = strata.getCacheDir().toPath().resolve(VINEFLOWER_FILE_NAME).toString();
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java", "-jar", vineflowerPath,
                    "--log-level=error",
                    "--synthetic-not-set=true",
                    "--ternary-constant-simplification=true",
                    "--include-runtime=current",
                    "--decompile-complex-constant-dynamic=true",
                    "--indent-string=    ",
                    "--decompile-inner=true",
                    "--remove-bridge=true",
                    "--decompile-generics=true",
                    "--ascii-strings=false",
                    "--remove-synthetic=true",
                    "--include-classpath=true",
                    "--inline-simple-lambdas=true",
                    "--ignore-invalid-bytecode=false",
                    "--bytecode-source-mapping=true",
                    "--dump-code-lines=true",
                    "--override-annotation=false",
                    inputJarPath,
                    outputDirPath
            );
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                strata.getLogger().error("Vineflower decompiler process exited with code: " + exitCode);
                return;
            }
        } catch (IOException | InterruptedException e) {
            strata.getLogger().error("Failed to start Vineflower decompiler process", ThrowableProperty.of(e));
        }

        strata.getLogger().info("Decompilation completed. Output directory: " + outputDirPath);
    }

    private void downloadVineflower() {
        Path vineflowerPath = strata.getCacheDir().toPath().resolve(VINEFLOWER_FILE_NAME);
        if (vineflowerPath.toFile().exists()) {
            return;
        }

        strata.getLogger().info("Downloading Vineflower decompiler...");

        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(VINEFLOWER_DOWNLOAD_URL))
                .build();

        try {
            HttpResponse<Path> resp = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build()
                    .send(req, HttpResponse.BodyHandlers.ofFile(vineflowerPath));
            if (resp.statusCode() != 200) {
                strata.getLogger().error("Failed to download Vineflower decompiler. HTTP status code: " + resp.statusCode());
                return;
            }

        } catch (IOException | InterruptedException e) {
            strata.getLogger().error("Failed to download Vineflower decompiler", ThrowableProperty.of(e));
        }

        strata.getLogger().info("Downloaded Vineflower decompiler to: " + vineflowerPath);
    }

}
