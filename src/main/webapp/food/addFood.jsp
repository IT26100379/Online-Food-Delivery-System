<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Food</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap -->
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

        <h2 class="page-title">Add New Food Item</h2>

        <!-- Error message -->
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

        <form action="<%= request.getContextPath() %>/food?action=add"
              method="post">

            <!-- Food ID -->
            <div class="mb-3">

                <label class="form-label">
                    Food ID
                </label>

                <input type="text"
                       name="foodId"
                       class="form-control"
                       placeholder="Example: F001"
                       required>

            </div>

            <!-- Food Name -->
            <div class="mb-3">

                <label class="form-label">
                    Food Name
                </label>

                <input type="text"
                       name="name"
                       class="form-control"
                       placeholder="Example: Chicken Burger"
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

                    <option value="">
                        Select Category
                    </option>

                    <option value="Burger">
                        Burger
                    </option>

                    <option value="Pizza">
                        Pizza
                    </option>

                    <option value="Rice">
                        Rice
                    </option>

                    <option value="Beverage">
                        Beverage
                    </option>

                    <option value="Dessert">
                        Dessert
                    </option>

                    <option value="Other">
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
                       placeholder="Example: 850"
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

                    <option value="true">
                        Available
                    </option>

                    <option value="false">
                        Not Available
                    </option>

                </select>

            </div>

            <!-- Buttons -->
            <div class="d-flex gap-2">

                <button type="submit"
                        class="btn btn-primary">
                    Add Food
                </button>

                <a href="<%= request.getContextPath() %>/food"
                   class="btn btn-secondary">
                    View Food List
                </a>

            </div>

        </form>

    </div>

</div>

</body>
</html>