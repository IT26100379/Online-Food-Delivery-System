
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Form</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }
        label {
            display: block;
            margin-top: 12px;
        }
        input, select {
            padding: 6px;
            width: 260px;
            box-sizing: border-box;
        }
        button {
            margin-top: 16px;
            padding: 8px 16px;
        }
    </style>
</head>
<body>

<h2>
    ${empty order ? 'New Order' : 'Edit Order'}
</h2>

<form action="${pageContext.request.contextPath}/orders"
      method="post">

    <input type="hidden"
           name="orderId"
           value="${fn:escapeXml(order.orderId)}">

    <label>User ID (e.g. U001)</label>
    <input type="text"
           name="userId"
           value="${fn:escapeXml(order.userId)}"
           required>

    <label>Food ID (e.g. F001)</label>
    <input type="text"
           name="foodId"
           value="${fn:escapeXml(order.foodId)}"
           required>

    <label>Quantity</label>
    <input type="number"
           name="quantity"
           min="1"
           value="${order.quantity}"
           required>

    <label>Total Price</label>
    <input type="number"
           name="totalPrice"
           min="0"
           step="0.01"
           value="${order.totalPrice}"
           required>

    <label>Status</label>
    <select name="status">

        <c:forEach var="s"
                   items="${fn:split('PLACED,PREPARING,DELIVERED,CANCELLED', ',')}">

            <option value="${s}"
                ${order.status eq s ? 'selected="selected"' : ''}>
                ${s}
            </option>

        </c:forEach>

    </select>

    <br>

    <button type="submit">Save</button>

    <a href="${pageContext.request.contextPath}/orders">
        Cancel
    </a>

</form>

</body>
</html>