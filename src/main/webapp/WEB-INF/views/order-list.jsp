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

        button.del {
            color: #c0392b;
            background: none;
            border: none;
            cursor: pointer;
        }

        form.inline {
            display: inline;
        }
    </style>
</head>

<body>

<h2>Order Management</h2>

<!-- Admin Only: New Order -->
<c:if test="${sessionScope.loggedUser.role == 'ADMIN'}">
    <p>
        <a class="btn"
           href="${pageContext.request.contextPath}/orders?action=new">
            + New Order
        </a>
    </p>
</c:if>

<table>
    <tr>
        <th>Order ID</th>
        <th>User ID</th>
        <th>Food ID</th>
        <th>Qty</th>
        <th>Total (Rs.)</th>
        <th>Status</th>
        <th>Date</th>

        <c:if test="${sessionScope.loggedUser.role == 'ADMIN'}">
            <th>Actions</th>
        </c:if>
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

            <!-- Admin Only: Edit / Delete -->
            <c:if test="${sessionScope.loggedUser.role == 'ADMIN'}">
                <td>
                    <c:url var="editUrl" value="/orders">
                        <c:param name="action" value="edit"/>
                        <c:param name="id" value="${o.orderId}"/>
                    </c:url>

                    <a href="${editUrl}">Edit</a>
                    |

                    <form class="inline"
                          method="post"
                          action="${pageContext.request.contextPath}/orders"
                          onsubmit="return confirm('Delete this order?');">

                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id"
                               value="<c:out value='${o.orderId}'/>">

                        <button class="del" type="submit">
                            Delete
                        </button>
                    </form>
                </td>
            </c:if>
        </tr>
    </c:forEach>

    <c:if test="${empty orders}">
        <tr>
            <td colspan="${sessionScope.loggedUser.role == 'ADMIN' ? 8 : 7}">
                No orders yet.
            </td>
        </tr>
    </c:if>
</table>

</body>
</html>