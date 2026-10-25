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
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/food-photos.css">


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

        <div class="yc-food-grid">
        <%
            if (foods != null && !foods.isEmpty()) {
                for (FoodItem food : foods) {
        %>
            <article class="yc-food-card">
                <div class="yc-food-photo-wrap">
                    <jsp:include page="/WEB-INF/views/common/food-photo.jsp">
                        <jsp:param name="foodName" value="<%= food.getName() %>" />
                        <jsp:param name="foodCategory" value="<%= food.getCategory() %>" />
                    </jsp:include>
                </div>
                <div class="yc-food-card-body">
                    <span class="yc-food-category"><%= food.getCategory().replace("&", "&amp;").replace("<", "&lt;") %></span>
                    <h3><%= food.getName().replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") %></h3>
                    <p class="yc-food-meta"><%= food.getDisplayType() %> · ID: <%= food.getFoodId() %></p>
                    <div class="yc-food-card-bottom">
                        <strong class="yc-food-price">Rs. <%= String.format("%.2f", food.getPrice()) %></strong>
                        <% if (food.isAvailable()) { %><span class="badge bg-success">Available</span>
                        <% } else { %><span class="badge bg-danger">Unavailable</span><% } %>
                    </div>
                    <% if (isAdmin) { %>
                    <div class="yc-food-card-actions">
                        <a href="<%= request.getContextPath() %>/food?action=edit&amp;id=<%= java.net.URLEncoder.encode(food.getFoodId(), java.nio.charset.StandardCharsets.UTF_8) %>" class="btn btn-warning btn-sm">Edit</a>
                        <form action="<%= request.getContextPath() %>/food" method="post" onsubmit="return confirm('Delete this food item?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="<%= food.getFoodId() %>">
                            <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                        </form>
                    </div>
                    <% } else if (food.isAvailable()) { %>
                        <a class="btn btn-primary yc-order-now" href="<%= request.getContextPath() %>/orders?action=place&amp;foodId=<%= java.net.URLEncoder.encode(food.getFoodId(), java.nio.charset.StandardCharsets.UTF_8) %>">Order now →</a>
                    <% } else { %><button class="btn yc-order-now" disabled>Currently unavailable</button><% } %>
                </div>
            </article>
        <% } } else { %>
            <div class="yc-food-empty">No food items found.</div>
        <% } %>
        </div>

    </div>

</div>


<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
