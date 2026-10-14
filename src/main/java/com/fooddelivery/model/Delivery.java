package com.fooddelivery.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** One delivery job: the order it serves, who carries it, where it goes, and how far along it is. */
public class Delivery {

    private static final Pattern ORDER_ID_FORMAT = Pattern.compile("O\\d{3,}");
    private static final Pattern PHONE_FORMAT = Pattern.compile("\\+?[0-9 ]{7,15}");
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int FIELD_COUNT = 8;

    private String deliveryId;
    private String orderId;
    private String driverName;
    private String driverPhone;
    private String deliveryAddress;
    private DeliveryStatus status;
    private int estimatedMinutes;
    private LocalDateTime createdAt;

    public Delivery() {
        this.deliveryId = "";
        this.orderId = "";
        this.driverName = "";
        this.driverPhone = "";
        this.deliveryAddress = "";
        this.status = DeliveryStatus.PENDING;
        this.estimatedMinutes = 30;
        this.createdAt = LocalDateTime.now();
    }

    // ---- Getters and setters (setters normalise input so the rest of the code never sees null) ----

    public String getDeliveryId() { return deliveryId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = clean(deliveryId); }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = clean(orderId).toUpperCase(); }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = clean(driverName); }

    public String getDriverPhone() { return driverPhone; }
    public void setDriverPhone(String driverPhone) { this.driverPhone = clean(driverPhone); }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = clean(deliveryAddress); }

    public DeliveryStatus getStatus() { return status; }
    public void setStatus(DeliveryStatus status) { this.status = status == null ? DeliveryStatus.PENDING : status; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt; }

    public String getCreatedAtDisplay() { return createdAt.format(DISPLAY_FORMAT); }

    public boolean hasDriver() { return !driverName.isBlank(); }

    // ---- Validation ----

    /** Returns every problem with this delivery; an empty list means it is valid. */
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (!ORDER_ID_FORMAT.matcher(orderId).matches()) {
            errors.add("Order ID must look like O001.");
        }
        if (driverName.length() > 60) {
            errors.add("Driver name must be 60 characters or fewer.");
        }
        if (status.requiresDriver() && !hasDriver()) {
            errors.add("Assign a driver before setting the status to " + status.getLabel() + ".");
        }
        if ((hasDriver() || !driverPhone.isBlank()) && !PHONE_FORMAT.matcher(driverPhone).matches()) {
            errors.add("Enter a valid driver phone number, for example 0771234567.");
        }
        if (deliveryAddress.length() < 5 || deliveryAddress.length() > 200) {
            errors.add("Delivery address must be between 5 and 200 characters.");
        }
        if (estimatedMinutes < 1 || estimatedMinutes > 240) {
            errors.add("Estimated time must be between 1 and 240 minutes.");
        }
        return errors;
    }

    // ---- File handling: one pipe-delimited line per delivery ----

    public String toFileString() {
        return String.join("|",
                escape(deliveryId),
                escape(orderId),
                escape(driverName),
                escape(driverPhone),
                escape(deliveryAddress),
                status.name(),
                String.valueOf(estimatedMinutes),
                createdAt.toString());
    }

    public static Delivery fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != FIELD_COUNT) {
            throw new IllegalArgumentException("Expected " + FIELD_COUNT + " fields but found " + p.length);
        }
        Delivery d = new Delivery();
        d.setDeliveryId(unescape(p[0]));
        d.setOrderId(unescape(p[1]));
        d.setDriverName(unescape(p[2]));
        d.setDriverPhone(unescape(p[3]));
        d.setDeliveryAddress(unescape(p[4]));
        DeliveryStatus st = DeliveryStatus.parse(p[5]);
        if (st == null) {
            throw new IllegalArgumentException("Unknown status '" + p[5] + "'");
        }
        d.setStatus(st);
        d.setEstimatedMinutes(Integer.parseInt(p[6]));
        d.setCreatedAt(LocalDateTime.parse(p[7]));
        return d;
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("|", "\\p").replace("\r", "").replace("\n", "\\n");
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                sb.append(next == 'p' ? '|' : next == 'n' ? '\n' : next);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Delivery{" + deliveryId + ", order=" + orderId + ", status=" + status + "}";
    }
}
