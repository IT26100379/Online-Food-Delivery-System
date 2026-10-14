package com.fooddelivery.service;

import java.util.Map;

/**
 * Gives the review feature access to the food list without depending on
 * the Food Management classes (which are on another branch).
 */
public interface FoodCatalog {

    /** Returns foodId -> food name, e.g. F001 -> Chicken Burger. */
    Map<String, String> getFoodNames();
}