package com.origamimc.strata.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void unzip(String zipFilePath, String destDir) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();

        byte[] buffer = new byte[4096];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry;

        while ((entry = zis.getNextEntry()) != null) {
            File newFile = new File(destDir, entry.getName());

            if (entry.isDirectory()) {
                newFile.mkdirs();
            } else {
                // create parent directories if needed
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }

            zis.closeEntry();
        }

        zis.close();
    }

    public static void injectToZip(String filePath, String zipFilePath) {
        File zipFile = new File(zipFilePath);
        File tempFile = new File(zipFilePath + ".tmp");
        byte[] buffer = new byte[4096];

        File fileToAdd = new File(filePath);
        if (!fileToAdd.exists() || !fileToAdd.isFile()) {
            return;
        }

        try (
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempFile))
        ) {

            // Copy existing entries
            if (zipFile.exists()) {
                try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
                    ZipEntry entry;

                    while ((entry = zis.getNextEntry()) != null) {
                        // Skip if same file name (optional overwrite behavior)
                        if (entry.getName().equals(fileToAdd.getName())) {
                            continue;
                        }

                        zos.putNextEntry(new ZipEntry(entry.getName()));

                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }

                        zos.closeEntry();
                    }
                }
            }

            // Add new file
            try (FileInputStream fis = new FileInputStream(fileToAdd)) {
                ZipEntry newEntry = new ZipEntry(fileToAdd.getName());
                zos.putNextEntry(newEntry);

                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                zos.closeEntry();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to update ZIP", e);
        }

        // Replace original ZIP
        if (!tempFile.renameTo(zipFile)) {
            throw new RuntimeException("Failed to replace original ZIP");
        }
    }

    public static void addFileToZip(String content, String fileName, String zipFilePath) {
        File tempFile = new File(zipFilePath + ".tmp");

        try (
                ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempFile))
        ) {
            ZipEntry entry;

            // Copy existing entries
            while ((entry = zis.getNextEntry()) != null) {
                zos.putNextEntry(new ZipEntry(entry.getName()));
                zis.transferTo(zos);
                zos.closeEntry();
            }

            // Add new file
            ZipEntry newEntry = new ZipEntry(fileName);
            zos.putNextEntry(newEntry);
            zos.write(content.getBytes(StandardCharsets.UTF_8));
            zos.closeEntry();

        } catch (IOException e) {
            throw new RuntimeException("Failed to update ZIP", e);
        }

        // Replace original ZIP
        if (!tempFile.renameTo(new File(zipFilePath))) {
            throw new RuntimeException("Failed to replace original ZIP file");
        }
    }

}
