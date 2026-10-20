package com.fooddelivery.model;

import java.time.LocalTime;

public class TraditionalRestaurant extends Restaurant {
    public TraditionalRestaurant(String id, String name, String location, String contact,
                                 LocalTime opening, LocalTime closing, boolean available) {
        super(id, name, location, contact, opening, closing, available);
    }

    @Override public String getType() { return "TRADITIONAL"; }
    @Override public String getTypeLabel() { return "Traditional"; }
    @Override public String getServiceDescription() { return "Local cuisine and home-style dishes"; }
}
