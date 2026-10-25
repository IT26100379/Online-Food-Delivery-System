package com.fooddelivery.controller;

import com.fooddelivery.model.FoodItem;
import com.fooddelivery.service.FoodService;
import com.fooddelivery.service.RestaurantService;
import com.fooddelivery.model.Restaurant;
import java.nio.file.Path;

import com.fooddelivery.model.User;
import com.fooddelivery.model.Role;
import jakarta.servlet.http.HttpSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/food")
@MultipartConfig(maxFileSize = 5 * 1024 * 1024, maxRequestSize = 6 * 1024 * 1024)
public class FoodServlet extends HttpServlet {

    private FoodService foodService;
    private RestaurantService restaurantService;

    @Override
    public void init() {

        String filePath =
                getServletContext().getRealPath("/WEB-INF/data/foods.txt");

        foodService = new FoodService(filePath);
        String dir = getServletContext().getInitParameter("fooddelivery.dataDir");
        if (dir == null || dir.isBlank()) dir = System.getProperty("fooddelivery.dataDir");
        Path dataDir = (dir == null || dir.isBlank())
                ? Path.of(System.getProperty("user.home"), "fooddelivery-data") : Path.of(dir);
        restaurantService = new RestaurantService(dataDir);
    }

    private boolean requireAdmin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        HttpSession session = request.getSession(false);

        User user = session == null
                ? null
                : (User) session.getAttribute("loggedUser");

        if (user == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return false;
        }

        if (user.getRole() != Role.ADMIN) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Access Denied! Admins Only."
            );
            return false;
        }

        return true;
    }

    // =========================
    // GET REQUESTS
    // =========================
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        if ("add".equals(action) || "edit".equals(action)) {
            if (!requireAdmin(request, response)) {
                return;
            }
        }


        switch (action) {

            case "add":
                showAddFoodPage(request, response);
                break;

            case "edit":
                showEditFoodPage(request, response);
                break;

            case "delete":
                response.sendError(
                        HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                        "Delete must use POST."
                );
                break;

            case "search":
                searchFood(request, response);
                break;

            default:
                listFoods(request, response);
                break;
        }
    }

    // =========================
    // POST REQUESTS
    // =========================
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "";
        }

        if (!requireAdmin(request, response)) {
            return;
        }

        switch (action) {

            case "add":
                addFood(request, response);
                break;

            case "update":
                updateFood(request, response);
                break;

            case "delete":
                deleteFood(request, response);
                break;

            default:
                response.sendRedirect(
                        request.getContextPath() + "/food"
                );
                break;
        }
    }

    // =========================
    // SHOW ADD FOOD PAGE
    // =========================
    private void showAddFoodPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setAttribute("restaurants", restaurantService.findAll());
        request.getRequestDispatcher(
                "/food/addFood.jsp"
        ).forward(request, response);
    }

    // =========================
    // CREATE
    // =========================
    private void addFood(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {

            String foodId = request.getParameter("foodId");
            String name = request.getParameter("name");
            String category = request.getParameter("category");

            double price = Double.parseDouble(
                    request.getParameter("price")
            );

            boolean available = Boolean.parseBoolean(
                    request.getParameter("available")
            );

            FoodItem food = new FoodItem(
                    foodId,
                    name,
                    category,
                    price,
                    available
            );

            String restaurantId = request.getParameter("restaurantId");
            if (restaurantId == null || restaurantService.findById(restaurantId).isEmpty()) {
                request.setAttribute("error", "Please select a valid restaurant.");
                showAddFoodPage(request, response);
                return;
            }
            food.setRestaurantId(restaurantId);
            Part photo = request.getPart("foodPhoto");
            // Validate uploaded image before adding food (empty image allowed).
            // saveUpload uses foodId so it can be replaced later in Edit Food.
            boolean success = foodService.addFood(food);

            if (success) {
                try {
                    FoodPhotoServlet.saveUpload(foodId, photo);
                } catch (IOException photoError) {
                    request.setAttribute("error", "Food was added, but photo could not be saved: " + photoError.getMessage());
                    showAddFoodPage(request, response);
                    return;
                }
                response.sendRedirect(
                        request.getContextPath() + "/food"
                );

            } else {

                request.setAttribute(
                        "error",
                        "Food ID already exists. Please use a different Food ID."
                );

                showAddFoodPage(request, response);
            }

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Please enter a valid price."
            );

            showAddFoodPage(request, response);
        }
    }

    // =========================
    // READ ALL
    // =========================
    private void listFoods(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String restaurantId = request.getParameter("restaurantId");
        List<FoodItem> foods = (restaurantId == null || restaurantId.isBlank())
                ? foodService.getAllFoods() : foodService.getFoodsByRestaurant(restaurantId);
        request.setAttribute("selectedRestaurantId", restaurantId);
        if (restaurantId != null && !restaurantId.isBlank()) {
            Restaurant restaurant = restaurantService.findById(restaurantId).orElse(null);
            request.setAttribute("selectedRestaurant", restaurant);
        }

        request.setAttribute(
                "foods",
                foods
        );

        request.getRequestDispatcher(
                "/food/foodList.jsp"
        ).forward(request, response);
    }

    // =========================
    // SHOW EDIT PAGE
    // =========================
    private void showEditFoodPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String foodId =
                request.getParameter("id");

        FoodItem food =
                foodService.getFoodById(foodId);

        if (food != null) {

            request.setAttribute("restaurants", restaurantService.findAll());
            request.setAttribute(
                    "food",
                    food
            );

            request.getRequestDispatcher(
                    "/food/editFood.jsp"
            ).forward(request, response);

        } else {

            response.sendRedirect(
                    request.getContextPath() + "/food"
            );
        }
    }

    // =========================
    // UPDATE
    // =========================
    private void updateFood(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        // Multipart forms can sometimes return null from getParameter().
        // Read text fields from their multipart Part as a fallback.
        String foodId = formValue(request, "foodId");
        String name = formValue(request, "name");
        String category = formValue(request, "category");
        String priceText = formValue(request, "price");
        String availabilityText = formValue(request, "available");
        String restaurantId = formValue(request, "restaurantId");

        if (foodId == null || foodId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Food ID is missing.");
            return;
        }

        if (name == null || name.isBlank() || category == null || category.isBlank()) {
            showEditError(request, response, foodId, "Please enter a food name and category.");
            return;
        }

        if (priceText == null || priceText.isBlank()) {
            showEditError(request, response, foodId, "Price was not received. Please try again.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText.trim());
        } catch (NumberFormatException e) {
            showEditError(request, response, foodId, "Please enter a valid price.");
            return;
        }
        if (!Double.isFinite(price) || price < 0) {
            showEditError(request, response, foodId, "Please enter a valid non-negative price.");
            return;
        }

        if (availabilityText == null || !(availabilityText.equals("true") || availabilityText.equals("false"))) {
            showEditError(request, response, foodId, "Please select availability.");
            return;
        }

        if (restaurantId == null || restaurantId.isBlank()
                || restaurantService.findById(restaurantId).isEmpty()) {
            showEditError(request, response, foodId, "Please select a valid restaurant.");
            return;
        }

        if (foodService.getFoodById(foodId) == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Food item not found.");
            return;
        }

        FoodItem updatedFood = new FoodItem(
                foodId, name, category, price, Boolean.parseBoolean(availabilityText)
        );
        updatedFood.setRestaurantId(restaurantId);
        foodService.updateFood(updatedFood);

        try {
            FoodPhotoServlet.saveUpload(foodId, request.getPart("foodPhoto"));
        } catch (IOException photoError) {
            showEditError(request, response, foodId,
                    "Food was updated, but photo could not be saved: " + photoError.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/food");
    }

    private String formValue(HttpServletRequest request, String name)
            throws ServletException, IOException {
        String value = request.getParameter(name);
        if (value != null) return value;
        Part part = request.getPart(name);
        if (part == null) return null;
        try (var input = part.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void showEditError(HttpServletRequest request,
                               HttpServletResponse response,
                               String foodId, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("food", foodService.getFoodById(foodId));
        request.setAttribute("restaurants", restaurantService.findAll());
        request.getRequestDispatcher("/food/editFood.jsp").forward(request, response);
    }

    // =========================
    // DELETE
    // =========================
    private void deleteFood(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String foodId =
                request.getParameter("id");

        if (foodService.deleteFood(foodId)) {
            FoodPhotoServlet.delete(foodId);
        }

        response.sendRedirect(
                request.getContextPath() + "/food"
        );
    }

    // =========================
    // SEARCH
    // =========================
    private void searchFood(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String keyword =
                request.getParameter("keyword");

        List<FoodItem> foods =
                foodService.searchFoods(keyword);

        request.setAttribute(
                "foods",
                foods
        );

        request.setAttribute(
                "keyword",
                keyword
        );

        request.getRequestDispatcher(
                "/food/foodList.jsp"
        ).forward(request, response);
    }
}
