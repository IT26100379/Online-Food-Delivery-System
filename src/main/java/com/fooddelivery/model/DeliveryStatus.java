package com.fooddelivery.model;

/** Stages a delivery moves through, in order, plus the CANCELLED exit. */
public enum DeliveryStatus {
    PENDING("Pending"),
    ASSIGNED("Assigned"),
    PICKED_UP("Picked up"),
    ON_THE_WAY("On the way"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled");

    private final String label;

    DeliveryStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /** True once the delivery can no longer change stage. */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }

    /** True for stages that only make sense when a driver is attached. */
    public boolean requiresDriver() {
        return this == ASSIGNED || this == PICKED_UP || this == ON_THE_WAY || this == DELIVERED;
    }

    /** The next stage in the normal flow, or null when there is none. */
    public DeliveryStatus getNext() {
        switch (this) {
            case PENDING:    return ASSIGNED;
            case ASSIGNED:   return PICKED_UP;
            case PICKED_UP:  return ON_THE_WAY;
            case ON_THE_WAY: return DELIVERED;
            default:         return null;
        }
    }

    /** Parses a status name; returns null for blank or unknown input. */
    public static DeliveryStatus parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
