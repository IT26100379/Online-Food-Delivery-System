package com.fooddelivery.model;

public class Beverage extends FoodItem {

    public Beverage(String foodId, String name, String category,
                    double price, boolean available) {

        super(foodId, name, category, price, available);
    }

    @Override
    public String getDisplayType() {
        return "Beverage";
    }
}