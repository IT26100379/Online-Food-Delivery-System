package com.fooddelivery.service;

import com.fooddelivery.model.FoodItem;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FoodService {

    private final String filePath;

    public FoodService(String filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
    }

    // Create foods.txt automatically if it does not exist
    private void createFileIfNotExists() {
        try {
            File file = new File(filePath);

            File parent = file.getParentFile();

            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

        } catch (IOException e) {
            System.err.println("Error creating food file: " + e.getMessage());
        }
    }

    // =========================
    // CREATE
    // =========================
    // CREATE
    public boolean addFood(FoodItem food) {

        // Duplicate Food ID check
        if (getFoodById(food.getFoodId()) != null) {
            return false;
        }

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(filePath, true))) {

            writer.write(food.toString());
            writer.newLine();

            return true;

        } catch (IOException e) {
            System.err.println("Error adding food: " + e.getMessage());
            return false;
        }
    }

    // =========================
    // READ ALL
    // =========================
    public List<FoodItem> getAllFoods() {

        List<FoodItem> foods = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",");

                if (data.length == 5) {

                    String foodId = data[0];
                    String name = data[1];
                    String category = data[2];
                    double price = Double.parseDouble(data[3]);
                    boolean available = Boolean.parseBoolean(data[4]);

                    FoodItem food = new FoodItem(
                            foodId,
                            name,
                            category,
                            price,
                            available
                    );

                    foods.add(food);
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading foods: " + e.getMessage());
        }

        return foods;
    }

    // =========================
    // READ / SEARCH BY ID
    // =========================
    public FoodItem getFoodById(String foodId) {

        for (FoodItem food : getAllFoods()) {

            if (food.getFoodId().equalsIgnoreCase(foodId)) {
                return food;
            }
        }

        return null;
    }

    // =========================
    // SEARCH BY NAME / CATEGORY
    // =========================
    public List<FoodItem> searchFoods(String keyword) {

        List<FoodItem> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllFoods();
        }

        String searchText = keyword.trim().toLowerCase();

        for (FoodItem food : getAllFoods()) {

            if (food.getName().toLowerCase().contains(searchText)
                    || food.getCategory().toLowerCase().contains(searchText)
                    || food.getFoodId().toLowerCase().contains(searchText)) {

                results.add(food);
            }
        }

        return results;
    }

    // =========================
    // UPDATE
    // =========================
    public boolean updateFood(FoodItem updatedFood) {

        List<FoodItem> foods = getAllFoods();
        boolean found = false;

        for (int i = 0; i < foods.size(); i++) {

            if (foods.get(i)
                    .getFoodId()
                    .equalsIgnoreCase(updatedFood.getFoodId())) {

                foods.set(i, updatedFood);
                found = true;
                break;
            }
        }

        if (found) {
            return rewriteFile(foods);
        }

        return false;
    }

    // =========================
    // DELETE
    // =========================
    public boolean deleteFood(String foodId) {

        List<FoodItem> foods = getAllFoods();

        boolean removed = foods.removeIf(
                food -> food.getFoodId().equalsIgnoreCase(foodId)
        );

        if (removed) {
            return rewriteFile(foods);
        }

        return false;
    }

    // =========================
    // REWRITE FILE
    // =========================
    private boolean rewriteFile(List<FoodItem> foods) {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(filePath))) {

            for (FoodItem food : foods) {
                writer.write(food.toString());
                writer.newLine();
            }

            return true;

        } catch (IOException e) {
            System.err.println("Error rewriting food file: " + e.getMessage());
            return false;
        }
    }
}