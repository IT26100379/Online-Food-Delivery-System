<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Order form"/></jsp:include>
<main class="page-shell narrow" id="main-content">
  <a href="${pageContext.request.contextPath}/orders" class="section-link">← Back to orders</a>
  <div class="sub-hero"><span class="eyebrow">ORDER CENTER</span><h1>${empty order ? 'New order' : 'Edit order'}</h1><p>Enter the order information below.</p></div>
  <div class="card">
    <form action="${pageContext.request.contextPath}/orders" method="post">
      <input type="hidden" name="orderId" value="${fn:escapeXml(order.orderId)}">
      <label for="userId">User ID</label><input id="userId" type="text" name="userId" placeholder="e.g. U001" value="${fn:escapeXml(order.userId)}" required>
      <label for="foodId">Food ID</label><input id="foodId" type="text" name="foodId" placeholder="e.g. F001" value="${fn:escapeXml(order.foodId)}" required>
      <label for="quantity">Quantity</label><input id="quantity" type="number" name="quantity" min="1" value="${order.quantity}" required>
      <label for="totalPrice">Total price (Rs.)</label><input id="totalPrice" type="number" name="totalPrice" min="0" step="0.01" value="${order.totalPrice}" required>
      <label for="status">Status</label><select id="status" name="status"><c:forEach var="s" items="${fn:split('PLACED,PREPARING,DELIVERED,CANCELLED', ',')}"><option value="${s}" ${order.status eq s ? 'selected="selected"' : ''}>${s}</option></c:forEach></select>
      <div class="form-actions"><button class="btn" type="submit">Save order</button><a class="btn btn-light" href="${pageContext.request.contextPath}/orders">Cancel</a></div>
    </form>
  </div>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
