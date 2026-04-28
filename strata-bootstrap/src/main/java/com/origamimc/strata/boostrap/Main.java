package com.origamimc.strata.boostrap;

import com.origamimc.strata.Strata;
import com.origamimc.strata.mojang.PistonVersionDetails;
import com.origamimc.strata.utils.ResourceUtils;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    static void main() {
        Strata strata = new Strata(() -> "strata-cache", () -> "");
        strata.init();

        // Read version and fetch details from Mojang API
        String versionID = readVersion();
        if (versionID == null) {
            strata.getLogger().error("Could not read version.txt");
            return;
        }
        PistonVersionDetails version = strata.getMojangService().getVersion(versionID);
        if (version == null) {
            return;
        }

        // Download server jar and libraries
        if (!strata.getMojangService().downloadServerBundle(version)) {
            return;
        }
        if (!strata.getExtractorService().extractServerBundle(version.id())) {
            return;
        }

        // Patch server jar file
        if (!patchedJarExists(version)) {
            if (!Files.exists(Path.of("versions"))) {
                try {
                    Files.createDirectory(Path.of("versions"));
                } catch (IOException e) {
                    strata.getLogger().error(
                            "Failed to create versions directory",
                            ThrowableProperty.of(e)
                    );
                    return;
                }
            }

            strata.getLogger().info("Patching server jar for version " + version.id());

            byte[] patchBytes = readJarPatch(version);
            if (patchBytes == null) {
                strata.getLogger().error(
                        "Failed to read patch file for version",
                        StringProperty.of("version", version.id())
                );
                return;
            }

            strata.getPatcherService().patchJar(
                    strata.getExtractorService().getServerJarPath(version.id()),
                    "versions/server-" + version.id() + ".jar",
                    patchBytes
            );
        } else {
            strata.getLogger().info("Patched server jar already exists, skipping patching");
        }

        strata.getLogger().info("Starting server for version " + version.id());

        // Start server
        runServerJar(version, strata);
    }

    /**
     * Reads version.txt from the current jar and returns the version string
     */
    private static String readVersion() {
        return new ResourceUtils().readResource("version.txt");
    }

    /**
     * @return whether the patched jar for the given version already exists in the cache (at versions/server-<version>.jar)
     */
    private static boolean patchedJarExists(PistonVersionDetails version) {
        return Files.exists(Path.of("versions/server-" + version.id() + ".jar"));
    }

    /**
     * Reads the patch file for the given version from the resources and returns it as a byte array.
     * The patch file should be located at "server-<version>.patch" in the resources.
     */
    private static byte[] readJarPatch(PistonVersionDetails version) {
        return new ResourceUtils().readResourceBytes("server-" + version.id() + ".patch");
    }

    /**
     * Loads (patched) server jar to own classloader and run Minecraft main
     */
    private static void runServerJar(PistonVersionDetails version, Strata strata) {
        String serverJarPath = "versions/server-" + version.id() + ".jar";
        try {
            String librariesPath = strata.getExtractorService().getLibrariesPath(version.id());
            List<File> libs = Files.walk(Path.of(librariesPath))
                    .filter((file) -> Files.isRegularFile(file) && file.toString().endsWith(".jar"))
                    .map(Path::toFile)
                    .toList();
            if (libs.isEmpty()) {
                strata.getLogger().error(
                        "Failed to list library files",
                        StringProperty.of("libraries_path", librariesPath)
                );
                return;
            }

            URL[] urls = new URL[libs.size() + 1];
            for (int i = 0; i < libs.size(); i++) {
                urls[i] = libs.get(i).toURI().toURL();
            }
            urls[libs.size()] = new File(serverJarPath).toURI().toURL();

            URLClassLoader classLoader = new URLClassLoader(urls, Main.class.getClassLoader());

            Class<?> mainClass = classLoader.loadClass("net.minecraft.server.Main");
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[]{});
        } catch (Exception e) {
            strata.getLogger().error(
                    "Failed to start server",
                    ThrowableProperty.of(e)
            );
        }
    }
}
