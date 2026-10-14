<%@ page import="java.util.List" %>
<%@ page import="com.fooddelivery.model.FoodItem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Food Management</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f5f5f5;
        }

        .main-container {
            margin-top: 50px;
            margin-bottom: 50px;
        }

        .table-container {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
        }

        .page-title {
            font-weight: bold;
        }
    </style>
</head>

<body>

<div class="container main-container">

    <div class="table-container">

        <div class="d-flex justify-content-between align-items-center mb-4">

            <h2 class="page-title">
                Food Management
            </h2>

            <a href="<%= request.getContextPath() %>/food?action=add"
               class="btn btn-primary">
                + Add Food
            </a>

        </div>

        <!-- Search Form -->
        <form action="<%= request.getContextPath() %>/food"
              method="get"
              class="row g-2 mb-4">

            <input type="hidden"
                   name="action"
                   value="search">

            <div class="col-md-8">

                <input type="text"
                       name="keyword"
                       class="form-control"
                       placeholder="Search by Food ID, Name or Category"
                       value="<%= request.getAttribute("keyword") != null
                               ? request.getAttribute("keyword")
                               : "" %>">

            </div>

            <div class="col-md-2">

                <button type="submit"
                        class="btn btn-success w-100">
                    Search
                </button>

            </div>

            <div class="col-md-2">

                <a href="<%= request.getContextPath() %>/food"
                   class="btn btn-secondary w-100">
                    Reset
                </a>

            </div>

        </form>


        <%
            List<FoodItem> foods =
                    (List<FoodItem>) request.getAttribute("foods");
        %>

        <div class="table-responsive">

            <table class="table table-bordered table-hover align-middle">

                <thead class="table-dark">

                <tr>
                    <th>Food ID</th>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Type</th>
                    <th>Price (Rs.)</th>
                    <th>Availability</th>
                    <th>Actions</th>
                </tr>

                </thead>

                <tbody>

                <%
                    if (foods != null && !foods.isEmpty()) {

                        for (FoodItem food : foods) {
                %>

                <tr>

                    <td>
                        <%= food.getFoodId() %>
                    </td>

                    <td>
                        <%= food.getName() %>
                    </td>

                    <td>
                        <%= food.getCategory() %>
                    </td>

                    <td>
                        <%= food.getDisplayType() %>
                    </td>

                    <td>
                        <%= String.format("%.2f", food.getPrice()) %>
                    </td>

                    <td>

                        <%
                            if (food.isAvailable()) {
                        %>

                        <span class="badge bg-success">
                            Available
                        </span>

                        <%
                        } else {
                        %>

                        <span class="badge bg-danger">
                            Not Available
                        </span>

                        <%
                            }
                        %>

                    </td>

                    <td>

                        <a href="<%= request.getContextPath() %>/food?action=edit&id=<%= food.getFoodId() %>"
                           class="btn btn-warning btn-sm">
                            Edit
                        </a>

                        <a href="<%= request.getContextPath() %>/food?action=delete&id=<%= food.getFoodId() %>"
                           class="btn btn-danger btn-sm"
                           onclick="return confirm('Are you sure you want to delete this food item?');">
                            Delete
                        </a>

                    </td>

                </tr>

                <%
                    }

                } else {
                %>

                <tr>

                    <td colspan="7"
                        class="text-center text-muted">

                        No food items found.

                    </td>

                </tr>

                <%
                    }
                %>

                </tbody>

            </table>

        </div>

    </div>

</div>

</body>
</html>