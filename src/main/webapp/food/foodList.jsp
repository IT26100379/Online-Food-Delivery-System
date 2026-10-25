<%@ page import="java.util.List" %>
<%@ page import="com.fooddelivery.model.FoodItem" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="com.fooddelivery.model.User" %>
<%@ page import="com.fooddelivery.model.Role" %>

<%
    Object currentUser = session.getAttribute("loggedUser");

    boolean isAdmin = currentUser instanceof User
            && ((User) currentUser).getRole() == Role.ADMIN;
%>

<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Food menu"/></jsp:include>


<div class="container main-container wide" id="main-content">

    <div class="table-container">

        <div class="d-flex justify-content-between align-items-center mb-4">

            <h2 class="page-title">
                Food menu
                <% if (request.getAttribute("selectedRestaurant") != null) { %>
                    — <%= ((com.fooddelivery.model.Restaurant) request.getAttribute("selectedRestaurant")).getName() %>
                <% } %>
            </h2>

            <% if (isAdmin) { %>

                <a href="<%= request.getContextPath() %>/food?action=add"
                   class="btn btn-primary">
                    + Add Food
                </a>

            <% } %>

        </div>

        <% if (request.getAttribute("selectedRestaurantId") != null) { %>
            <a class="btn btn-outline-secondary mb-3" href="<%= request.getContextPath() %>/restaurants">← Back to Restaurants</a>
        <% } %>
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
                        class="btn btn-primary w-100">
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

                <thead>

                <tr>
                    <th>Food ID</th>
                    <th>Name</th>
                    <th>Category</th>
                    <th>Type</th>
                    <th>Price (Rs.)</th>
                    <th>Availability</th>

                    <% if (isAdmin) { %>
                         <th>Actions</th>
                     <% } else { %>
                         <th>Order</th>
                     <% } %>

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

                    <% if (isAdmin) { %>

                    <td>
                        <div class="d-flex gap-2">

                            <!-- Edit Button -->
                            <a href="<%= request.getContextPath() %>/food?action=edit&id=<%= food.getFoodId() %>"
                               class="btn btn-warning btn-sm">
                                Edit
                            </a>

                            <!-- Delete Button -->
                            <form action="<%= request.getContextPath() %>/food"
                                  method="post"
                                  onsubmit="return confirm('Are you sure you want to delete this food item?');">

                                <input type="hidden" name="action" value="delete">

                                <input type="hidden"
                                       name="id"
                                       value="<%= food.getFoodId() %>">

                                <button type="submit" class="btn btn-danger btn-sm">
                                    Delete
                                </button>

                            </form>

                        </div>
                    </td>

                    <% } else { %>
                    <td>
                        <% if (food.isAvailable()) { %>
                        <a class="btn btn-primary btn-sm" href="<%= request.getContextPath() %>/orders?action=place&amp;foodId=<%= java.net.URLEncoder.encode(food.getFoodId(), java.nio.charset.StandardCharsets.UTF_8) %>">Order now</a>
                        <% } else { %><span class="text-muted">Unavailable</span><% } %>
                    </td>
                    <% } %>
                </tr>



                <%
                    }

                } else {
                %>

                <tr>

                    <td colspan="<%= 7 %>"
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


<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
