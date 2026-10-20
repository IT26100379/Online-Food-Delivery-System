package com.fooddelivery.model;

import java.time.LocalTime;

public class FastFoodRestaurant extends Restaurant {
    public FastFoodRestaurant(String id, String name, String location, String contact,
                              LocalTime opening, LocalTime closing, boolean available) {
        super(id, name, location, contact, opening, closing, available);
    }

    @Override public String getType() { return "FAST_FOOD"; }
    @Override public String getTypeLabel() { return "Fast Food"; }
    @Override public String getServiceDescription() { return "Quick meals and takeaway favourites"; }
}
