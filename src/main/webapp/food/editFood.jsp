<%@ page import="java.util.List,com.fooddelivery.model.Restaurant" %>
<%@ page import="com.fooddelivery.model.FoodItem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    FoodItem food = (FoodItem) request.getAttribute("food");
%>

<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Edit food"/></jsp:include>


<div class="container" id="main-content">

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
              method="post" enctype="multipart/form-data">

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

            <!-- Restaurant association -->
            <div class="mb-3">
                <label class="form-label">Restaurant</label>
                <select name="restaurantId" class="form-select" required>
                    <option value="">Select Restaurant</option>
                    <%
                        List<Restaurant> restaurants = (List<Restaurant>) request.getAttribute("restaurants");
                        if (restaurants != null) for (Restaurant restaurant : restaurants) {
                    %>
                    <option value="<%= restaurant.getRestaurantId() %>" <%= restaurant.getRestaurantId().equals(food.getRestaurantId()) ? "selected" : "" %>><%= restaurant.getName() %></option>
                    <% } %>
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

            <!-- Uploaded photo is optional. Stored outside the deployed WAR. -->
            <div class="mb-3">
                <label class="form-label" for="foodPhoto">Food Photo (optional)</label>
                <input type="file" id="foodPhoto" name="foodPhoto" class="form-control"
                       accept="image/jpeg,image/png">
                <small class="text-muted">Leave empty to keep the existing photo. JPG or PNG, max 5 MB. Images appear on the menu and checkout.</small>
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
                   class="btn btn-light">
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
           class="btn btn-light">
            Back to Food List
        </a>

        <%
            }
        %>

    </div>

</div>


<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
