package com.origamimc.strata.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class ResourceUtils {

    public static byte[] readResourceToBytes(String resourcePath) {
        try (InputStream input = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            return input.readAllBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read resource: " + resourcePath, e);
        }
    }

}
