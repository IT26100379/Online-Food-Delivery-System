<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Online Food Delivery</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/header.jsp" />

<main style="max-width:1000px;margin:50px auto;padding:20px;">
    <h1>Welcome to Online Food Delivery</h1>
    <p>Explore restaurants, browse food and manage your orders.</p>

    <p>
        <a href="${pageContext.request.contextPath}/restaurants">Restaurants</a>
        |
        <a href="${pageContext.request.contextPath}/food">Foods</a>
        |
        <a href="${pageContext.request.contextPath}/orders">Orders</a>
        |
        <a href="${pageContext.request.contextPath}/delivery">Deliveries</a>
        |
        <a href="${pageContext.request.contextPath}/reviews">Reviews</a>
    </p>

    <c:if test="${sessionScope.loggedUser.role == 'ADMIN'}">
        <p>
            <a href="${pageContext.request.contextPath}/admin/dashboard">
                Admin Dashboard
            </a>
        </p>
    </c:if>

    <c:if test="${empty sessionScope.loggedUser}">
        <p>
            <a href="${pageContext.request.contextPath}/login">Login</a>
            |
            <a href="${pageContext.request.contextPath}/register">Register</a>
        </p>
    </c:if>
</main>

</body>
</html>