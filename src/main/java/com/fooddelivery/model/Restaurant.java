package com.fooddelivery.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Base class for restaurants: encapsulation + abstraction. */
public abstract class Restaurant {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private String restaurantId;
    private String name;
    private String location;
    private String contactNumber;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private boolean available;

    protected Restaurant(String restaurantId, String name, String location,
                         String contactNumber, LocalTime openingTime,
                         LocalTime closingTime, boolean available) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.location = location;
        this.contactNumber = contactNumber;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.available = available;
    }

    /** Subclasses return their storage category (inheritance / polymorphism). */
    public abstract String getType();
    public abstract String getTypeLabel();
    public abstract String getServiceDescription();

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public LocalTime getOpeningTime() { return openingTime; }
    public void setOpeningTime(LocalTime openingTime) { this.openingTime = openingTime; }
    public LocalTime getClosingTime() { return closingTime; }
    public void setClosingTime(LocalTime closingTime) { this.closingTime = closingTime; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getStatusLabel() { return available ? "Open" : "Closed"; }
    public String getOpeningTimeText() { return openingTime == null ? "" : openingTime.format(TIME_FORMAT); }
    public String getClosingTimeText() { return closingTime == null ? "" : closingTime.format(TIME_FORMAT); }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank() || name.length() > 80) {
            errors.add("Restaurant name is required (maximum 80 characters).");
        }
        if (location == null || location.isBlank() || location.length() > 120) {
            errors.add("Location is required (maximum 120 characters).");
        }
        if (contactNumber == null || !contactNumber.matches("\\+?[0-9]{9,15}")) {
            errors.add("Contact number must have 9 to 15 digits (optional leading +).");
        }
        if (openingTime == null || closingTime == null || !openingTime.isBefore(closingTime)) {
            errors.add("Enter valid opening and closing times (opening must be earlier; same-day hours only).");
        }
        // One restaurant per file line. Reject delimiters and newlines before writing.
        for (String field : new String[]{name, location, contactNumber}) {
            if (field != null && (field.contains("|") || field.contains("\n") || field.contains("\r"))) {
                errors.add("Text fields cannot contain | or line breaks.");
                break;
            }
        }
        return errors;
    }

    public String toFileLine() {
        if (!validate().isEmpty()) {
            throw new IllegalStateException("Cannot serialize an invalid restaurant.");
        }
        return String.join("|", restaurantId, getType(), name, location, contactNumber,
                getOpeningTimeText(), getClosingTimeText(), Boolean.toString(available));
    }

    public static Restaurant fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 8) {
            throw new IllegalArgumentException("Invalid restaurant record format.");
        }
        LocalTime opening = LocalTime.parse(parts[5], TIME_FORMAT);
        LocalTime closing = LocalTime.parse(parts[6], TIME_FORMAT);
        boolean available;
        if ("true".equalsIgnoreCase(parts[7])) {
            available = true;
        } else if ("false".equalsIgnoreCase(parts[7])) {
            available = false;
        } else {
            throw new IllegalArgumentException("Invalid availability flag.");
        }
        Restaurant restaurant = switch (parts[1]) {
            case "FAST_FOOD" -> new FastFoodRestaurant(parts[0], parts[2], parts[3], parts[4], opening, closing, available);
            case "TRADITIONAL" -> new TraditionalRestaurant(parts[0], parts[2], parts[3], parts[4], opening, closing, available);
            default -> throw new IllegalArgumentException("Unknown restaurant type: " + parts[1]);
        };
        if (!restaurant.validate().isEmpty()) {
            throw new IllegalArgumentException("Restaurant record failed validation.");
        }
        return restaurant;
    }
}
