<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${param.title} - Food Delivery</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav>
    <a class="brand" href="${pageContext.request.contextPath}/">Food Delivery</a>
    <div>
        <c:choose>
            <c:when test="${not empty sessionScope.loggedUser}">
                <c:if test="${sessionScope.loggedUser.role == 'ADMIN'}">
                    <a href="${pageContext.request.contextPath}/admin/users">Manage Users</a>
                </c:if>
                <a href="${pageContext.request.contextPath}/profile">My Profile</a>
                <form class="inline" method="post" action="${pageContext.request.contextPath}/logout">
                    <button type="submit">Logout</button>
                </form>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login">Login</a>
                <a href="${pageContext.request.contextPath}/register">Register</a>
            </c:otherwise>
        </c:choose>
    </div>
</nav>