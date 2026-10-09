<%@ page import="com.fooddelivery.model.FoodItem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    FoodItem food = (FoodItem) request.getAttribute("food");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Food</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f5f5f5;
        }

        .form-container {
            max-width: 650px;
            margin: 60px auto;
            background: white;
            padding: 35px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        .page-title {
            text-align: center;
            margin-bottom: 30px;
            font-weight: bold;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="form-container">

        <h2 class="page-title">
            Edit Food Item
        </h2>

        <%
            String error = (String) request.getAttribute("error");

            if (error != null) {
        %>

        <div class="alert alert-danger">
            <%= error %>
        </div>

        <%
            }
        %>

        <%
            if (food != null) {
        %>

        <form action="<%= request.getContextPath() %>/food?action=update"
              method="post">

            <!-- Food ID -->
            <div class="mb-3">

                <label class="form-label">
                    Food ID
                </label>

                <input type="text"
                       name="foodId"
                       class="form-control"
                       value="<%= food.getFoodId() %>"
                       readonly>

            </div>

            <!-- Food Name -->
            <div class="mb-3">

                <label class="form-label">
                    Food Name
                </label>

                <input type="text"
                       name="name"
                       class="form-control"
                       value="<%= food.getName() %>"
                       required>

            </div>

            <!-- Category -->
            <div class="mb-3">

                <label class="form-label">
                    Category
                </label>

                <select name="category"
                        class="form-select"
                        required>

                    <option value="Burger"
                            <%= "Burger".equals(food.getCategory()) ? "selected" : "" %>>
                        Burger
                    </option>

                    <option value="Pizza"
                            <%= "Pizza".equals(food.getCategory()) ? "selected" : "" %>>
                        Pizza
                    </option>

                    <option value="Rice"
                            <%= "Rice".equals(food.getCategory()) ? "selected" : "" %>>
                        Rice
                    </option>

                    <option value="Beverage"
                            <%= "Beverage".equals(food.getCategory()) ? "selected" : "" %>>
                        Beverage
                    </option>

                    <option value="Dessert"
                            <%= "Dessert".equals(food.getCategory()) ? "selected" : "" %>>
                        Dessert
                    </option>

                    <option value="Other"
                            <%= "Other".equals(food.getCategory()) ? "selected" : "" %>>
                        Other
                    </option>

                </select>

            </div>

            <!-- Price -->
            <div class="mb-3">

                <label class="form-label">
                    Price (Rs.)
                </label>

                <input type="number"
                       name="price"
                       class="form-control"
                       step="0.01"
                       min="0"
                       value="<%= food.getPrice() %>"
                       required>

            </div>

            <!-- Availability -->
            <div class="mb-3">

                <label class="form-label">
                    Availability
                </label>

                <select name="available"
                        class="form-select"
                        required>

                    <option value="true"
                            <%= food.isAvailable() ? "selected" : "" %>>
                        Available
                    </option>

                    <option value="false"
                            <%= !food.isAvailable() ? "selected" : "" %>>
                        Not Available
                    </option>

                </select>

            </div>

            <div class="d-flex gap-2">

                <button type="submit"
                        class="btn btn-warning">
                    Update Food
                </button>

                <a href="<%= request.getContextPath() %>/food"
                   class="btn btn-secondary">
                    Cancel
                </a>

            </div>

        </form>

        <%
            } else {
        %>

        <div class="alert alert-danger">
            Food item not found.
        </div>

        <a href="<%= request.getContextPath() %>/food"
           class="btn btn-secondary">
            Back to Food List
        </a>

        <%
            }
        %>

    </div>

</div>

</body>
</html>