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
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/food")
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
            boolean success = foodService.addFood(food);

            if (success) {

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

        try {

            String foodId =
                    request.getParameter("foodId");

            String name =
                    request.getParameter("name");

            String category =
                    request.getParameter("category");

            double price =
                    Double.parseDouble(
                            request.getParameter("price")
                    );

            boolean available =
                    Boolean.parseBoolean(
                            request.getParameter("available")
                    );

            FoodItem updatedFood =
                    new FoodItem(
                            foodId,
                            name,
                            category,
                            price,
                            available
                    );

            String restaurantId = request.getParameter("restaurantId");
            if (restaurantId == null || restaurantService.findById(restaurantId).isEmpty()) {
                request.setAttribute("error", "Please select a valid restaurant.");
                showEditFoodPage(request, response);
                return;
            }
            updatedFood.setRestaurantId(restaurantId);
            foodService.updateFood(updatedFood);

            response.sendRedirect(
                    request.getContextPath() + "/food"
            );

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Please enter a valid price."
            );

            showEditFoodPage(
                    request,
                    response
            );
        }
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

        foodService.deleteFood(foodId);

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