<%@ page import="java.util.List,com.fooddelivery.model.Restaurant" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Add food"/></jsp:include>


<div class="container" id="main-content">

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

            <!-- Restaurant association -->
            <div class="mb-3">
                <label class="form-label">Restaurant</label>
                <select name="restaurantId" class="form-select" required>
                    <option value="">Select Restaurant</option>
                    <%
                        List<Restaurant> restaurants = (List<Restaurant>) request.getAttribute("restaurants");
                        if (restaurants != null) for (Restaurant restaurant : restaurants) {
                    %>
                    <option value="<%= restaurant.getRestaurantId() %>" ><%= restaurant.getName() %></option>
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
                   class="btn btn-light">
                    View Food List
                </a>

            </div>

        </form>

    </div>

</div>


<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
