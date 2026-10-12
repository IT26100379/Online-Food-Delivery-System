
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Management</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: left;
        }
        th {
            background: #f4f4f4;
        }
        a.btn {
            padding: 8px 12px;
            background: #2a7de1;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        a.del {
            color: #c0392b;
        }
    </style>
</head>
<body>

<h2>Order Management</h2>

<p>
    <a class="btn"
       href="${pageContext.request.contextPath}/orders?action=new">
        + New Order
    </a>
</p>

<table>
    <tr>
        <th>Order ID</th>
        <th>User ID</th>
        <th>Food ID</th>
        <th>Qty</th>
        <th>Total (Rs.)</th>
        <th>Status</th>
        <th>Date</th>
        <th>Actions</th>
    </tr>

    <c:forEach var="o" items="${orders}">
        <tr>
            <td><c:out value="${o.orderId}"/></td>
            <td><c:out value="${o.userId}"/></td>
            <td><c:out value="${o.foodId}"/></td>
            <td><c:out value="${o.quantity}"/></td>
            <td><c:out value="${o.totalPrice}"/></td>
            <td><c:out value="${o.status}"/></td>
            <td><c:out value="${o.orderDate}"/></td>
            <td>
                <c:url var="editUrl" value="/orders">
                    <c:param name="action" value="edit"/>
                    <c:param name="id" value="${o.orderId}"/>
                </c:url>

                <a href="${editUrl}">Edit</a>
                |
                <c:url var="deleteUrl" value="/orders">
                    <c:param name="action" value="delete"/>
                    <c:param name="id" value="${o.orderId}"/>
                </c:url>

                <a class="del"
                   href="${deleteUrl}"
                   onclick="return confirm('Delete this order?');">
                    Delete
                </a>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty orders}">
        <tr>
            <td colspan="8">No orders yet.</td>
        </tr>
    </c:if>
</table>

</body>
</html>