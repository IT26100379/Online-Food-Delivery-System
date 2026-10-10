package com.fooddelivery.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public final class FileHandler {

    private static final Path DATA_DIR = Paths.get(
            System.getProperty("fooddelivery.data.dir",
                    System.getProperty("user.home") + "/fooddelivery-data"));

    private FileHandler() {}

    public static List<String> readLines(String fileName) {
        try {
            Files.createDirectories(DATA_DIR);
            Path file = DATA_DIR.resolve(fileName);
            if (!Files.exists(file)) {
                return List.of();
            }
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + fileName, e);
        }
    }

    public static void writeLines(String fileName, List<String> lines) {
        try {
            Files.createDirectories(DATA_DIR);
            Path file = DATA_DIR.resolve(fileName);
            Path tmp = DATA_DIR.resolve(fileName + ".tmp");
            Files.write(tmp, lines, StandardCharsets.UTF_8);
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write " + fileName, e);
        }
    }
}