package com.fooddelivery.service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TEMPORARY: sample foods for testing reviews until the Food Management
 * branch is merged. Replace with a real FoodCatalog later.
 */
public class SampleFoodCatalog implements FoodCatalog {

    @Override
    public Map<String, String> getFoodNames() {
        Map<String, String> foods = new LinkedHashMap<>();
        foods.put("F001", "Chicken Burger");
        foods.put("F002", "Chocolate Brownie");
        foods.put("F003", "Iced Coffee");
        return foods;
    }
}