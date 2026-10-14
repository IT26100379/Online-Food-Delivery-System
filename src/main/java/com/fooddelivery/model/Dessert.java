package com.fooddelivery.model;

public class Dessert extends FoodItem {

    public Dessert(String foodId, String name, String category,
                   double price, boolean available) {

        super(foodId, name, category, price, available);
    }

    @Override
    public String getDisplayType() {
        return "Dessert";
    }
}