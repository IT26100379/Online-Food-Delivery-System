package com.fooddelivery.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Locally stored food photos, independent from foods.txt and Tomcat redeploys. */
@WebServlet("/food-photo")
public class FoodPhotoServlet extends HttpServlet {
    private static final int MAX_BYTES = 5 * 1024 * 1024;

    private static Path folder() throws IOException {
        String configured = System.getProperty("fooddelivery.dataDir");
        Path base = configured == null || configured.isBlank()
                ? Path.of(System.getProperty("user.home"), "fooddelivery-data")
                : Path.of(configured);
        Path result = base.resolve("food-photos");
        Files.createDirectories(result);
        return result;
    }

    private static String hashId(String foodId) {
        if (foodId == null || foodId.isBlank()) throw new IllegalArgumentException("Food ID required");
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(foodId.trim().toLowerCase(java.util.Locale.ROOT).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }

    /** Validates type, real image content, and size before saving. Empty upload leaves previous photo unchanged. */
    public static void saveUpload(String foodId, Part part) throws IOException {
        if (part == null || part.getSize() == 0) return;
        if (part.getSize() > MAX_BYTES) throw new IOException("Photo must be 5 MB or smaller");
        String type = part.getContentType();
        String ext;
        if ("image/jpeg".equalsIgnoreCase(type)) ext = ".jpg";
        else if ("image/png".equalsIgnoreCase(type)) ext = ".png";
        else throw new IOException("Only JPG and PNG photos are supported");
        byte[] bytes;
        try (var in = part.getInputStream()) { bytes = in.readNBytes(MAX_BYTES + 1); }
        if (bytes.length > MAX_BYTES) throw new IOException("Photo must be 5 MB or smaller");
        BufferedImage image = ImageIO.read(new java.io.ByteArrayInputStream(bytes));
        if (image == null || image.getWidth() > 6000 || image.getHeight() > 6000)
            throw new IOException("Invalid image or dimensions too large");
        Path directory = folder();
        String stem = hashId(foodId);
        Path target = directory.resolve(stem + ext);
        Path temporary = Files.createTempFile(directory, "upload-", ".tmp");
        try {
            Files.write(temporary, bytes);
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(directory.resolve(stem + (".jpg".equals(ext) ? ".png" : ".jpg")));
        } finally { Files.deleteIfExists(temporary); }
    }

    public static void delete(String foodId) throws IOException {
        String stem = hashId(foodId);
        Files.deleteIfExists(folder().resolve(stem + ".jpg"));
        Files.deleteIfExists(folder().resolve(stem + ".png"));
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (id == null || id.isBlank()) { resp.sendError(404); return; }
        Path dir = folder();
        String stem = hashId(id);
        Path jpg = dir.resolve(stem + ".jpg");
        Path png = dir.resolve(stem + ".png");
        Path photo = Files.isRegularFile(jpg) ? jpg : (Files.isRegularFile(png) ? png : null);
        if (photo == null) { resp.sendError(404); return; }
        resp.setContentType(photo.equals(jpg) ? "image/jpeg" : "image/png");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setContentLengthLong(Files.size(photo));
        Files.copy(photo, resp.getOutputStream());
    }
}
