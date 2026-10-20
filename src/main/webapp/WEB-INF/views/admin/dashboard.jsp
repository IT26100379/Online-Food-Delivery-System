<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <h1>Admin Dashboard</h1>

    <div>
        <h3>Total Users</h3>
        <p>${totalUsers}</p>
    </div>

    <<p>
         <a href="${pageContext.request.contextPath}/admin/users">Manage Users</a> |
         <a href="${pageContext.request.contextPath}/admin/restaurants">Manage Restaurants</a> |
         <a href="${pageContext.request.contextPath}/logout">Logout</a>
     </p>
</body>
</html>