package com.fooddelivery.model;

public class MainMeal extends FoodItem {

    public MainMeal(String foodId, String name, String category,
                    double price, boolean available) {

        super(foodId, name, category, price, available);
    }

    @Override
    public String getDisplayType() {
        return "Main Meal";
    }
}