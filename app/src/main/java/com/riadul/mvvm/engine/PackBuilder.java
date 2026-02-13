package com.riadul.mvvm.engine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles the compilation of Minecraft Add-ons into .mcaddon files.
 * Uses Scoped Storage compatible paths.
 */
public class PackBuilder {

    public record BuildResult(boolean success, String filePath, String errorMessage) {}

    /**
     * Zips the contents of a directory into a Minecraft-ready package.
     * @param sourceDir The temporary directory containing manifest.json, etc.
     * @param outPath Where the .mcpack/.mcaddon should be saved.
     */
    public static BuildResult build(File sourceDir, String outPath) {
        // Validation using Java 23 Pattern Matching for the File object
        if (!(sourceDir instanceof File dir) || !dir.exists() || !dir.isDirectory()) {
            return new BuildResult(false, null, "Source directory is invalid.");
        }

        try (FileOutputStream fos = new FileOutputStream(outPath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ZipOutputStream zos = new ZipOutputStream(bos)) {

            File[] files = sourceDir.listFiles();
            if (files == null) return new BuildResult(false, null, "No files found to pack.");

            for (File file : files) {
                addToZip(file, file.getName(), zos);
            }

            return new BuildResult(true, outPath, null);

        } catch (IOException e) {
            return new BuildResult(false, null, "Zip Error: " + e.getMessage());
        }
    }

    private static void addToZip(File file, String fileName, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                addToZip(child, fileName + "/" + child.getName(), zos);
            }
            return;
        }

        // Using nio.Files for efficient reading
        byte[] content = Files.readAllBytes(file.toPath());
        ZipEntry entry = new ZipEntry(fileName);
        zos.putNextEntry(entry);
        zos.write(content);
        zos.closeEntry();
    }
}
