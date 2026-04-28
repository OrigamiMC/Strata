package com.origamimc.strata.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ResourceUtils {

    /**
     * Reads a resource from the classpath and returns its content as a string.
     *
     * @param path the path to the resource, relative to the classpath. For example, if the resource is at "src/main/resources/version.txt", the path should be "version.txt".
     * @return the content of the resource as a string, or null if the resource could not be found or read.
     */
    public String readResource(String path) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) {
            return null;
        }

        URLConnection connection = null;
        try {
            connection = url.openConnection();
            connection.setUseCaches(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (InputStream inputStream = connection.getInputStream()) {
            byte[] file_raw = new byte[inputStream.available()];
            inputStream.read(file_raw);
            inputStream.close();
            return new String(file_raw, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Reads a resource from the classpath and returns its content as a byte array.
     */
    public byte[] readResourceBytes(String path) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url == null) {
            return null;
        }

        URLConnection connection = null;
        try {
            connection = url.openConnection();
            connection.setUseCaches(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try  {
        InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[16 * 1024];
            int off = 0;
            int read;
            while ((read = inputStream.read(buffer, off, buffer.length - off)) != -1) {
                off += read;
                if (off == buffer.length) {
                    buffer = Arrays.copyOf(buffer, buffer.length * 2);
                }
            }
            return Arrays.copyOfRange(buffer, 0, off);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
