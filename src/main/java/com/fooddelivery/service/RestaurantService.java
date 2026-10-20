package com.fooddelivery.service;

import com.fooddelivery.model.Restaurant;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Restaurant CRUD. Uses one UTF-8 record per line in restaurants.txt. */
public class RestaurantService {
    private final Path dataFile;

    public RestaurantService(Path dataDir) {
        try {
            Files.createDirectories(dataDir);
            dataFile = dataDir.resolve("restaurants.txt");
            if (Files.notExists(dataFile)) {
                Files.createFile(dataFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot initialise restaurant data", e);
        }
    }

    // CREATE: assign R001, R002, ...; reject malformed records.
    public synchronized Restaurant create(Restaurant restaurant) {
        requireValid(restaurant);
        List<Restaurant> all = readAll();
        restaurant.setRestaurantId(nextId(all));
        all.add(restaurant);
        writeAll(all);
        return restaurant;
    }

    // READ
    public synchronized List<Restaurant> findAll() {
        List<Restaurant> all = readAll();
        all.sort(Comparator.comparing(Restaurant::getRestaurantId));
        return all;
    }

    public synchronized Optional<Restaurant> findById(String id) {
        if (id == null) return Optional.empty();
        return readAll().stream()
                .filter(r -> r.getRestaurantId().equalsIgnoreCase(id))
                .findFirst();
    }

    public synchronized List<Restaurant> search(String keyword) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<Restaurant> results = new ArrayList<>();
        for (Restaurant restaurant : findAll()) {
            if (key.isEmpty() || restaurant.getRestaurantId().toLowerCase(Locale.ROOT).contains(key)
                    || restaurant.getName().toLowerCase(Locale.ROOT).contains(key)
                    || restaurant.getLocation().toLowerCase(Locale.ROOT).contains(key)
                    || restaurant.getTypeLabel().toLowerCase(Locale.ROOT).contains(key)) {
                results.add(restaurant);
            }
        }
        return results;
    }

    // UPDATE
    public synchronized boolean update(Restaurant updated) {
        requireValid(updated);
        List<Restaurant> all = readAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getRestaurantId().equalsIgnoreCase(updated.getRestaurantId())) {
                all.set(i, updated);
                writeAll(all);
                return true;
            }
        }
        return false;
    }

    // DELETE
    public synchronized boolean delete(String id) {
        if (id == null || id.isBlank()) return false;
        List<Restaurant> all = readAll();
        boolean removed = all.removeIf(r -> r.getRestaurantId().equalsIgnoreCase(id));
        if (removed) writeAll(all);
        return removed;
    }

    private void requireValid(Restaurant restaurant) {
        List<String> errors = restaurant.validate();
        if (!errors.isEmpty()) throw new IllegalArgumentException(String.join(" ", errors));
    }

    private String nextId(List<Restaurant> all) {
        int largest = 0;
        for (Restaurant restaurant : all) {
            String id = restaurant.getRestaurantId();
            if (id.matches("R[0-9]+")) {
                largest = Math.max(largest, Integer.parseInt(id.substring(1)));
            }
        }
        return String.format("R%03d", largest + 1);
    }

    private List<Restaurant> readAll() {
        try {
            List<Restaurant> restaurants = new ArrayList<>();
            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                try {
                    restaurants.add(Restaurant.fromFileLine(line));
                } catch (RuntimeException e) {
                    // Do not silently skip broken records: rewriting would lose them.
                    throw new IllegalStateException("restaurants.txt line " + (i + 1) + " is invalid", e);
                }
            }
            return restaurants;
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read restaurants.txt", e);
        }
    }

    private void writeAll(List<Restaurant> restaurants) {
        Path temporary = null;
        try {
            temporary = Files.createTempFile(dataFile.getParent(), "restaurants-", ".tmp");
            List<String> lines = new ArrayList<>();
            for (Restaurant restaurant : restaurants) lines.add(restaurant.toFileLine());
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, dataFile, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot save restaurants.txt", e);
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); } catch (IOException ignored) { }
            }
        }
    }
}
