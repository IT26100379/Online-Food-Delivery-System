package com.fooddelivery.controller;

import com.fooddelivery.model.FoodItem;
import com.fooddelivery.service.FoodService;

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

    @Override
    public void init() {

        String filePath =
                getServletContext().getRealPath("/WEB-INF/data/foods.txt");

        foodService = new FoodService(filePath);
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

        switch (action) {

            case "add":
                showAddFoodPage(request, response);
                break;

            case "edit":
                showEditFoodPage(request, response);
                break;

            case "delete":
                deleteFood(request, response);
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

        switch (action) {

            case "add":
                addFood(request, response);
                break;

            case "update":
                updateFood(request, response);
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

            boolean success = foodService.addFood(food);

            if (success) {

                response.sendRedirect(
                        request.getContextPath() + "/food"
                );

            } else {

                request.setAttribute(
                        "error",
                        "Failed to add food item."
                );

                request.getRequestDispatcher(
                        "/food/addFood.jsp"
                ).forward(request, response);
            }

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Please enter a valid price."
            );

            request.getRequestDispatcher(
                    "/food/addFood.jsp"
            ).forward(request, response);
        }
    }

    // =========================
    // READ ALL
    // =========================
    private void listFoods(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        List<FoodItem> foods =
                foodService.getAllFoods();

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