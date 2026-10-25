<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"><jsp:param name="title" value="Orders"/></jsp:include>
<main class="page" id="main-content">
  <div class="page-top"><div><span class="eyebrow">ORDER CENTER</span><h1 class="page-heading">Order management</h1><p>A simple overview of every order and its current status.</p></div>
    <c:if test="${sessionScope.loggedUser.role == 'ADMIN'}"><a class="btn" href="${pageContext.request.contextPath}/orders?action=new">+ New order</a></c:if>
  </div>
  <section class="panel"><div class="section-heading"><div><h3>All orders</h3><p class="muted">Your current order list</p></div><span class="pill">▤ &nbsp; Orders</span></div>
    <div class="table-wrap"><table>
      <thead><tr><th>Order ID</th><th>User ID</th><th>Food ID</th><th>Qty</th><th>Total (Rs.)</th><th>Status</th><th>Date</th><c:if test="${sessionScope.loggedUser.role == 'ADMIN'}"><th>Actions</th></c:if></tr></thead>
      <tbody>
        <c:forEach var="o" items="${orders}"><tr>
          <td class="id"><c:out value="${o.orderId}"/></td><td><c:out value="${o.userId}"/></td><td><c:out value="${o.foodId}"/></td>
          <td><c:out value="${o.quantity}"/></td><td><strong><c:out value="${o.totalPrice}"/></strong></td>
          <td><span class="badge"><c:out value="${o.status}"/></span></td><td><c:out value="${o.orderDate}"/></td>
          <c:if test="${sessionScope.loggedUser.role == 'ADMIN'}"><td><div class="actions">
            <c:url var="editUrl" value="/orders"><c:param name="action" value="edit"/><c:param name="id" value="${o.orderId}"/></c:url>
            <a class="btn btn-small btn-light" href="${editUrl}">Edit</a>
            <form class="inline" method="post" action="${pageContext.request.contextPath}/orders" onsubmit="return confirm('Delete this order?');">
              <input type="hidden" name="action" value="delete"><input type="hidden" name="id" value="<c:out value='${o.orderId}'/>">
              <button class="btn btn-small btn-danger" type="submit">Delete</button>
            </form></div></td></c:if>
        </tr></c:forEach>
        <c:if test="${empty orders}"><tr><td class="empty" colspan="${sessionScope.loggedUser.role == 'ADMIN' ? 8 : 7}">No orders yet.</td></tr></c:if>
      </tbody>
    </table></div>
  </section>
</main>
<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
</body></html>
