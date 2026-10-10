package com.fooddelivery.service;

import com.fooddelivery.model.Delivery;
import com.fooddelivery.model.DeliveryStatus;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create, read, update and delete deliveries, stored one per line in {dataDir}/deliveries.txt.
 * All public methods are synchronized because the servlet container shares one instance across requests.
 */
public class DeliveryService {

    private static final Pattern ID_FORMAT = Pattern.compile("D(\\d+)");

    private final Path dataFile;

    public DeliveryService(Path dataDir) {
        try {
            Files.createDirectories(dataDir);
            this.dataFile = dataDir.resolve("deliveries.txt");
            if (Files.notExists(dataFile)) {
                Files.createFile(dataFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot set up delivery storage in " + dataDir, e);
        }
    }

    // ---- Create ----

    /** Validates and saves a new delivery, assigning the next ID (D001, D002, ...). */
    public synchronized Delivery create(Delivery delivery) {
        requireValid(delivery);
        List<Delivery> all = readAll();
        rejectDuplicateActiveDelivery(all, delivery);

        delivery.setDeliveryId(nextId(all));
        delivery.setCreatedAt(LocalDateTime.now());
        all.add(delivery);
        writeAll(all);
        return delivery;
    }

    // ---- Read ----

    public synchronized List<Delivery> findAll() {
        List<Delivery> all = readAll();
        all.sort(Comparator.comparing(Delivery::getDeliveryId));
        return all;
    }

    public synchronized Optional<Delivery> findById(String id) {
        return readAll().stream().filter(d -> d.getDeliveryId().equalsIgnoreCase(id)).findFirst();
    }

    /** Filters by keyword (ID, order, driver, address) and/or status; either may be null or blank. */
    public synchronized List<Delivery> search(String keyword, DeliveryStatus status) {
        String needle = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        List<Delivery> result = new ArrayList<>();
        for (Delivery d : findAll()) {
            boolean statusOk = status == null || d.getStatus() == status;
            boolean textOk = needle.isEmpty()
                    || d.getDeliveryId().toLowerCase(Locale.ROOT).contains(needle)
                    || d.getOrderId().toLowerCase(Locale.ROOT).contains(needle)
                    || d.getDriverName().toLowerCase(Locale.ROOT).contains(needle)
                    || d.getDeliveryAddress().toLowerCase(Locale.ROOT).contains(needle);
            if (statusOk && textOk) {
                result.add(d);
            }
        }
        return result;
    }

    // ---- Update ----

    /** Replaces the stored delivery with the same ID. Returns false if it does not exist. */
    public synchronized boolean update(Delivery updated) {
        requireValid(updated);
        List<Delivery> all = readAll();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getDeliveryId().equalsIgnoreCase(updated.getDeliveryId())) {
                rejectDuplicateActiveDelivery(all, updated);
                updated.setCreatedAt(all.get(i).getCreatedAt());
                all.set(i, updated);
                writeAll(all);
                return true;
            }
        }
        return false;
    }

    /** Changes only the status of one delivery. Returns false if it does not exist. */
    public synchronized boolean updateStatus(String id, DeliveryStatus newStatus) {
        Optional<Delivery> existing = findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        Delivery d = existing.get();
        d.setStatus(newStatus);
        return update(d);
    }

    // ---- Delete ----

    /** Removes a delivery. Returns false if it does not exist. */
    public synchronized boolean delete(String id) {
        List<Delivery> all = readAll();
        boolean removed = all.removeIf(d -> d.getDeliveryId().equalsIgnoreCase(id));
        if (removed) {
            writeAll(all);
        }
        return removed;
    }

    // ---- Rules ----

    private void requireValid(Delivery delivery) {
        List<String> errors = delivery.validate();
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" ", errors));
        }
    }

    /** An order can only have one live delivery at a time. */
    private void rejectDuplicateActiveDelivery(List<Delivery> all, Delivery candidate) {
        if (candidate.getStatus().isTerminal()) {
            return;
        }
        for (Delivery d : all) {
            boolean sameOrder = d.getOrderId().equals(candidate.getOrderId());
            boolean otherRecord = !d.getDeliveryId().equalsIgnoreCase(candidate.getDeliveryId());
            if (sameOrder && otherRecord && !d.getStatus().isTerminal()) {
                throw new IllegalStateException(
                        "Order " + candidate.getOrderId() + " already has an active delivery (" + d.getDeliveryId() + ").");
            }
        }
    }

    private String nextId(List<Delivery> all) {
        int max = 0;
        for (Delivery d : all) {
            Matcher m = ID_FORMAT.matcher(d.getDeliveryId());
            if (m.matches()) {
                max = Math.max(max, Integer.parseInt(m.group(1)));
            }
        }
        return String.format("D%03d", max + 1);
    }

    // ---- File access ----

    private List<Delivery> readAll() {
        List<Delivery> list = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    list.add(Delivery.fromFileString(line));
                } catch (RuntimeException e) {
                    // Fail loudly: skipping the line would delete it on the next save.
                    throw new IllegalStateException(
                            "deliveries.txt line " + (i + 1) + " is damaged: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + dataFile, e);
        }
        return list;
    }

    /** Writes to a temp file first, then swaps it in, so a crash cannot leave a half-written data file. */
    private void writeAll(List<Delivery> deliveries) {
        List<String> lines = new ArrayList<>();
        for (Delivery d : deliveries) {
            lines.add(d.toFileString());
        }
        try {
            Path temp = Files.createTempFile(dataFile.getParent(), "deliveries", ".tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temp, dataFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, dataFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write " + dataFile, e);
        }
    }
}
